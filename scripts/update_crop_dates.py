"""
Updates crops.json + emits Migration6to7.kt with research-backed BOP planting
dates from docs/PAPAMOA_CROP_DATA_RESEARCH.md. Run from repo root.
"""
import json
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parent.parent
JSON_PATH = REPO / "app/src/main/assets/seed/crops.json"
MIGRATION_PATH = REPO / "app/src/main/java/nz/keeleysgreenhouse/app/data/migrations/Migration6to7.kt"

YEAR_ROUND = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
EMPTY = []

# (gh_sow, gh_plant, gh_harvest, out_sow, out_plant, out_harvest)
DATES = {
    # Group 1 — Solanaceae
    "Tomato":              ([6, 7, 8, 9],     [8, 9, 10, 11],  [12, 1, 2, 3, 4, 5],   [9, 10],         [10, 11],       [1, 2, 3, 4]),
    "Tomato (cherry)":     ([6, 7, 8, 9],     [8, 9, 10, 11],  [12, 1, 2, 3, 4, 5],   [9, 10],         [10, 11],       [1, 2, 3, 4]),
    "Tomato (beefsteak)":  ([6, 7, 8, 9],     [8, 9, 10, 11],  [12, 1, 2, 3, 4, 5],   [9, 10],         [10, 11],       [1, 2, 3, 4]),
    "Tomato (roma/paste)": ([6, 7, 8, 9],     [8, 9, 10, 11],  [12, 1, 2, 3, 4, 5],   [9, 10],         [10, 11],       [1, 2, 3, 4]),
    "Capsicum":            ([7, 8],           [9, 10, 11],     [1, 2, 3, 4, 5],       [8, 9],          [10, 11],       [1, 2, 3, 4]),
    "Chilli":              ([6, 7, 8],        [9, 10, 11],     [1, 2, 3, 4, 5, 6],    [8, 9],          [10, 11],       [1, 2, 3, 4, 5]),
    "Eggplant":            ([7, 8],           [9, 10, 11],     [1, 2, 3, 4, 5],       [9, 10],         [10, 11],       [1, 2, 3, 4]),
    "Tomatillo":           ([8, 9, 10],       [10, 11, 12],    [1, 2, 3, 4],          [9, 10],         [10, 11],       [1, 2, 3]),
    "Cape gooseberry":     ([8, 9, 10],       [10, 11, 12],    [2, 3, 4, 5],          [9, 10],         [10, 11],       [2, 3, 4]),
    "Pepino":              (EMPTY,            [9, 10, 11, 12], [1, 2, 3, 4, 5],       EMPTY,           [10, 11],       [2, 3, 4]),
    "Okra":                ([9, 10, 11],      [10, 11, 12],    [1, 2, 3, 4],          EMPTY,           [11, 12],       [2, 3, 4]),

    # Group 2 — Cucurbits
    "Cucumber":            ([8, 9, 10, 11, 12, 1], [9, 10, 11, 12, 1, 2], [11, 12, 1, 2, 3, 4], [10, 11, 12], [10, 11, 12], [1, 2, 3, 4]),
    "Gherkin":             ([8, 9, 10, 11, 12, 1], [9, 10, 11, 12, 1, 2], [11, 12, 1, 2, 3, 4], [10, 11, 12], [10, 11, 12], [1, 2, 3, 4]),
    "Zucchini":            ([9, 10, 11, 12, 1], [10, 11, 12, 1, 2], [11, 12, 1, 2, 3, 4], [10, 11, 12, 1], [10, 11, 12, 1], [12, 1, 2, 3, 4]),
    "Marrow":              ([9, 10, 11, 12, 1], [10, 11, 12, 1, 2], [11, 12, 1, 2, 3, 4], [10, 11, 12, 1], [10, 11, 12, 1], [12, 1, 2, 3, 4]),
    "Rockmelon":           ([9, 10, 11], [10, 11, 12], [2, 3, 4], [10, 11], [10, 11, 12], [2, 3, 4]),
    "Honeydew melon":      ([9, 10, 11], [10, 11, 12], [2, 3, 4], [10, 11], [10, 11, 12], [2, 3, 4]),
    "Watermelon (small)":  ([9, 10, 11], [10, 11, 12], [2, 3, 4], [10, 11], [11, 12],     [2, 3, 4]),
    "Pumpkin (Crown)":     ([9, 10, 11, 12], [10, 11, 12, 1], [3, 4, 5], [10, 11, 12, 1], [10, 11, 12, 1], [3, 4, 5]),
    "Buttercup squash":    ([9, 10, 11, 12], [10, 11, 12, 1], [3, 4, 5], [10, 11, 12, 1], [10, 11, 12, 1], [3, 4, 5]),
    "Butternut":           ([9, 10, 11, 12], [10, 11, 12, 1], [3, 4, 5], [10, 11, 12, 1], [10, 11, 12, 1], [3, 4, 5]),
    "Spaghetti squash":    ([9, 10, 11, 12], [10, 11, 12, 1], [3, 4, 5], [10, 11, 12, 1], [10, 11, 12, 1], [3, 4, 5]),
    "Luffa":               ([9, 10, 11], [10, 11, 12], [4, 5, 6], EMPTY, EMPTY, EMPTY),

    # Group 6 — Herbs
    "Basil (sweet)":       ([8, 9, 10, 11, 12, 1, 2], [9, 10, 11, 12, 1, 2, 3], [11, 12, 1, 2, 3, 4, 5], [10, 11, 12, 1, 2], [11, 12, 1, 2, 3], [12, 1, 2, 3, 4]),
    "Thai basil":          ([8, 9, 10, 11, 12, 1, 2], [9, 10, 11, 12, 1, 2, 3], [11, 12, 1, 2, 3, 4, 5], [10, 11, 12, 1, 2], [11, 12, 1, 2, 3], [12, 1, 2, 3, 4]),
    "Coriander":           ([2, 3, 4, 5, 8, 9, 10], [3, 4, 5, 6, 9, 10, 11], [3, 4, 5, 6, 10, 11, 12], [3, 4, 5, 9, 10], [3, 4, 5, 9, 10], [4, 5, 6, 10, 11]),
    "Parsley":             ([7, 8, 9, 10, 11, 12, 1, 2, 3, 4], [8, 9, 10, 11, 12, 1, 2, 3, 4, 5], YEAR_ROUND, [8, 9, 10, 11, 1, 2, 3, 4], [9, 10, 11, 1, 2, 3, 4, 5], YEAR_ROUND),
    "Mint":                ([9, 10, 11, 12, 1, 2, 3, 4], [9, 10, 11, 12, 1, 2, 3, 4], YEAR_ROUND, [9, 10, 11, 1, 2, 3, 4], [9, 10, 11, 1, 2, 3, 4], [9, 10, 11, 12, 1, 2, 3, 4, 5]),
    "Chives":              ([8, 9, 10, 11, 12, 1, 2, 3], [9, 10, 11, 12, 1, 2, 3], YEAR_ROUND, [9, 10, 11, 1, 2, 3], [9, 10, 11, 1, 2, 3], [9, 10, 11, 12, 1, 2, 3, 4, 5]),
    "Garlic chives":       ([8, 9, 10, 11, 12, 1, 2, 3], [9, 10, 11, 12, 1, 2, 3], YEAR_ROUND, [9, 10, 11, 1, 2, 3], [9, 10, 11, 1, 2, 3], [9, 10, 11, 12, 1, 2, 3, 4, 5]),
    "Dill":                ([9, 10, 11, 12, 1, 2, 3], EMPTY, [11, 12, 1, 2, 3, 4, 5], [10, 11, 12, 1, 2, 3], EMPTY, [12, 1, 2, 3, 4, 5]),
    "Oregano":             ([9, 10, 11, 12, 1, 2, 3], [9, 10, 11, 12, 1, 2, 3], YEAR_ROUND, [9, 10, 11, 1, 2, 3], [9, 10, 11, 1, 2, 3], YEAR_ROUND),
    "Thyme":               ([9, 10, 11, 12, 1, 2, 3], [9, 10, 11, 12, 1, 2, 3], YEAR_ROUND, [9, 10, 11, 1, 2, 3], [9, 10, 11, 1, 2, 3], YEAR_ROUND),
    "Rosemary":            (EMPTY, YEAR_ROUND, YEAR_ROUND, EMPTY, YEAR_ROUND, YEAR_ROUND),
    "Sage":                ([9, 10, 11, 12, 1, 2, 3], [9, 10, 11, 12, 1, 2, 3], YEAR_ROUND, [9, 10, 11, 1, 2, 3], [9, 10, 11, 1, 2, 3], YEAR_ROUND),
    "French tarragon":     (EMPTY, [9, 10, 11], [10, 11, 12, 1, 2, 3, 4], EMPTY, [9, 10, 11], [10, 11, 12, 1, 2, 3, 4]),
    "Lemon balm":          ([9, 10, 11, 12, 1, 2, 3], [9, 10, 11, 12, 1, 2, 3], YEAR_ROUND, [10, 11, 1, 2, 3], [10, 11, 1, 2, 3], [9, 10, 11, 12, 1, 2, 3, 4, 5]),
    "Lemongrass":          (EMPTY, [10, 11, 12], [2, 3, 4, 5], EMPTY, [11, 12], [3, 4, 5]),
    "Chervil":             ([2, 3, 4, 8, 9, 10], EMPTY, [4, 5, 6, 10, 11, 12], [3, 4, 5, 9, 10], EMPTY, [5, 6, 11, 12]),
    "Stevia":              ([10, 11, 12], [11, 12, 1], [2, 3, 4, 5], [11, 12], [12, 1], [2, 3, 4]),
    "Shiso":               ([9, 10, 11, 12, 1], [10, 11, 12, 1, 2], [12, 1, 2, 3, 4], [10, 11, 12, 1], [11, 12, 1, 2], [1, 2, 3, 4]),

    # Group 5 — Leafy greens
    "Lettuce (looseleaf)": ([2, 3, 4, 5, 8, 9, 10, 11], [3, 4, 5, 6, 9, 10, 11, 12], [4, 5, 6, 7, 10, 11, 12, 1], [3, 4, 5, 9, 10, 11], [4, 5, 6, 10, 11, 12], [5, 6, 7, 11, 12, 1]),
    "Cos":                 ([2, 3, 4, 5, 8, 9, 10], [3, 4, 5, 6, 9, 10, 11], [4, 5, 6, 7, 10, 11, 12], [3, 4, 5, 9, 10], [4, 5, 6, 10, 11], [5, 6, 7, 11, 12]),
    "Mesclun mix":         ([2, 3, 4, 5, 8, 9, 10, 11], [3, 4, 5, 6, 9, 10, 11, 12], [4, 5, 6, 7, 10, 11, 12, 1], [3, 4, 5, 9, 10, 11], [4, 5, 6, 10, 11, 12], [5, 6, 7, 11, 12, 1]),
    "Spinach":             ([2, 3, 4, 5, 6, 7, 8, 9], [3, 4, 5, 6, 7, 8, 9, 10], [5, 6, 7, 8, 9, 10, 11], [3, 4, 5, 6, 7, 8, 9], [4, 5, 6, 7, 8, 9, 10], [6, 7, 8, 9, 10, 11]),
    "NZ spinach":          ([9, 10, 11, 12, 1, 2, 3], [10, 11, 12, 1, 2, 3, 4], [11, 12, 1, 2, 3, 4, 5], [10, 11, 12, 1, 2, 3], [11, 12, 1, 2, 3, 4], [12, 1, 2, 3, 4, 5]),
    "Silverbeet":          ([2, 3, 4, 5, 8, 9, 10, 11, 12, 1], [3, 4, 5, 6, 9, 10, 11, 12, 1, 2], [4, 5, 6, 7, 10, 11, 12, 1, 2, 3], [3, 4, 5, 9, 10, 11], [4, 5, 6, 10, 11, 12], [6, 7, 8, 11, 12, 1]),
    "Rocket":              ([2, 3, 4, 5, 8, 9, 10, 11], [2, 3, 4, 5, 8, 9, 10, 11], [3, 4, 5, 6, 9, 10, 11, 12], [3, 4, 5, 9, 10, 11], [3, 4, 5, 9, 10, 11], [4, 5, 6, 10, 11, 12]),
    "Endive":              ([2, 3, 4, 5, 8, 9, 10], [3, 4, 5, 6, 9, 10, 11], [5, 6, 7, 10, 11, 12], [3, 4, 5, 9, 10], [4, 5, 6, 10, 11], [6, 7, 11, 12]),
    "Radicchio":           ([2, 3, 4, 5, 8, 9, 10], [3, 4, 5, 6, 9, 10, 11], [5, 6, 7, 10, 11, 12], [3, 4, 5, 9, 10], [4, 5, 6, 10, 11], [6, 7, 11, 12]),
    "Watercress":          ([9, 10, 11, 12, 1, 2, 3, 4], [9, 10, 11, 12, 1, 2, 3, 4], [11, 12, 1, 2, 3, 4, 5, 6], [10, 11, 12, 1, 2, 3, 4], [10, 11, 12, 1, 2, 3, 4], [12, 1, 2, 3, 4, 5, 6]),
    "Sorrel":              ([8, 9, 10, 11, 12, 1, 2, 3], [8, 9, 10, 11, 12, 1, 2, 3], [10, 11, 12, 1, 2, 3, 4, 5], [9, 10, 11, 1, 2, 3], [9, 10, 11, 1, 2, 3], [11, 12, 1, 2, 3, 4, 5]),
    "Corn salad":          ([3, 4, 5, 6, 7, 8, 9, 10], [3, 4, 5, 6, 7, 8, 9, 10], [5, 6, 7, 8, 9, 10, 11, 12], [4, 5, 6, 7, 8, 9, 10], [4, 5, 6, 7, 8, 9, 10], [6, 7, 8, 9, 10, 11, 12]),
    "Amaranth":            ([9, 10, 11, 12, 1, 2], [10, 11, 12, 1, 2, 3], [11, 12, 1, 2, 3, 4, 5], [10, 11, 12, 1, 2], [11, 12, 1, 2, 3], [12, 1, 2, 3, 4, 5]),
    "Microgreens (mix)":   (YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, EMPTY, EMPTY, EMPTY),

    # Group 4 — Brassicas
    "Kale":                ([1, 2, 3, 4, 7, 8, 9], [2, 3, 4, 5, 8, 9, 10], [4, 5, 6, 7, 10, 11, 12], [2, 3, 4, 8, 9], [3, 4, 5, 9, 10], [5, 6, 7, 11, 12]),
    "Broccoli":            ([1, 2, 3, 4, 7, 8, 9], [2, 3, 4, 5, 8, 9, 10], [4, 5, 6, 7, 10, 11, 12], [2, 3, 4, 8, 9], [3, 4, 5, 9, 10], [5, 6, 7, 11, 12]),
    "Broccolini":          ([1, 2, 3, 4, 7, 8, 9], [2, 3, 4, 5, 8, 9, 10], [4, 5, 6, 7, 10, 11, 12], [2, 3, 4, 8, 9], [3, 4, 5, 9, 10], [5, 6, 7, 11, 12]),
    "Cauliflower":         ([1, 2, 3, 7, 8], [2, 3, 4, 8, 9], [5, 6, 7, 10, 11], [2, 3, 8], [3, 4, 9], [6, 7, 11]),
    "Cabbage":             ([1, 2, 3, 4, 8, 9, 10], [2, 3, 4, 5, 9, 10, 11], [5, 6, 7, 8, 11, 12, 1], [2, 3, 4, 9, 10], [3, 4, 5, 10, 11], [6, 7, 8, 12, 1]),
    "Chinese cabbage":     ([2, 3, 4, 5, 8, 9, 10], [3, 4, 5, 6, 9, 10, 11], [4, 5, 6, 7, 10, 11, 12], [3, 4, 5, 9, 10], [4, 5, 6, 10, 11], [6, 7, 11, 12]),
    "Bok choy":            ([2, 3, 4, 5, 8, 9, 10], [3, 4, 5, 6, 9, 10, 11], [4, 5, 6, 7, 10, 11, 12], [3, 4, 5, 9, 10], [4, 5, 6, 10, 11], [5, 6, 7, 11, 12]),
    "Choi sum":            ([2, 3, 4, 5, 8, 9, 10], [3, 4, 5, 6, 9, 10, 11], [4, 5, 6, 7, 10, 11, 12], [3, 4, 5, 9, 10], [4, 5, 6, 10, 11], [5, 6, 7, 11, 12]),
    "Tatsoi":              ([2, 3, 4, 5, 8, 9, 10], [3, 4, 5, 6, 9, 10, 11], [4, 5, 6, 7, 10, 11, 12], [3, 4, 5, 9, 10], [4, 5, 6, 10, 11], [5, 6, 7, 11, 12]),
    "Mizuna":              ([2, 3, 4, 5, 8, 9, 10], [3, 4, 5, 6, 9, 10, 11], [4, 5, 6, 7, 10, 11, 12], [3, 4, 5, 9, 10], [4, 5, 6, 10, 11], [5, 6, 7, 11, 12]),
    "Mustard greens":      ([2, 3, 4, 5, 8, 9, 10], [3, 4, 5, 6, 9, 10, 11], [4, 5, 6, 7, 10, 11, 12], [3, 4, 5, 9, 10], [4, 5, 6, 10, 11], [5, 6, 7, 11, 12]),
    "Kohlrabi":            ([1, 2, 3, 4, 8, 9, 10], [2, 3, 4, 5, 9, 10, 11], [4, 5, 6, 11, 12], [2, 3, 4, 9, 10], [3, 4, 5, 10, 11], [5, 6, 11, 12]),
    "Brussels sprouts":    ([10, 11, 12, 1], [11, 12, 1, 2], [5, 6, 7, 8], [10, 11, 12], [11, 12, 1], [5, 6, 7, 8]),
    "Turnip":              ([2, 3, 4, 8, 9, 10], [2, 3, 4, 8, 9, 10], [4, 5, 6, 10, 11, 12], [2, 3, 4, 8, 9, 10], [2, 3, 4, 8, 9, 10], [4, 5, 6, 10, 11, 12]),
    "Swede":               ([2, 3, 4, 8, 9, 10], [2, 3, 4, 8, 9, 10], [4, 5, 6, 10, 11, 12], [2, 3, 4, 8, 9, 10], [2, 3, 4, 8, 9, 10], [4, 5, 6, 10, 11, 12]),
    "Daikon":              ([2, 3, 4, 8, 9, 10], [2, 3, 4, 8, 9, 10], [4, 5, 6, 10, 11, 12], [2, 3, 4, 8, 9, 10], [2, 3, 4, 8, 9, 10], [4, 5, 6, 10, 11, 12]),
    "Radish":              (YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND),

    # Group 7 — Roots
    "Carrot":              (YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND),
    "Beetroot":            (YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND),
    "Parsnip":             ([8, 9, 10, 11, 12, 1, 2, 3], [8, 9, 10, 11, 12, 1, 2, 3], [12, 1, 2, 3, 4, 5, 6, 7], [8, 9, 10, 11, 12, 1, 2, 3], [8, 9, 10, 11, 12, 1, 2, 3], [12, 1, 2, 3, 4, 5, 6, 7]),
    "Celeriac":            ([8, 9, 10], [9, 10, 11], [2, 3, 4, 5], [9, 10], [10, 11], [3, 4, 5]),
    "Potato":              ([7, 8, 9, 10, 11, 12], [7, 8, 9, 10, 11, 12], [11, 12, 1, 2, 3, 4, 5], [7, 8, 9, 10, 11, 12], [7, 8, 9, 10, 11, 12], [11, 12, 1, 2, 3, 4, 5]),
    "Kūmara":              ([10, 11, 12], [10, 11, 12], [4, 5], [10, 11, 12], [10, 11, 12], [4, 5]),
    "Oca":                 ([9, 10, 11], [9, 10, 11], [6, 7, 8], [9, 10, 11], [9, 10, 11], [6, 7, 8]),
    "Jerusalem artichoke": ([8, 9, 10], [8, 9, 10], [5, 6, 7, 8], [8, 9, 10], [8, 9, 10], [5, 6, 7, 8]),
    "Horseradish":         ([6, 7, 8], [6, 7, 8], [5, 6, 7, 8], [6, 7, 8], [6, 7, 8], [5, 6, 7, 8]),  # UNMATCHED — perennial root, BOP-typical pattern

    # Group 8 — Alliums
    "Onion":               ([3, 4, 5, 6, 7], [4, 5, 6, 7, 8], [12, 1, 2], [3, 4, 5, 6, 7], [4, 5, 6, 7, 8], [12, 1, 2]),
    "Spring onion":        (YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND, YEAR_ROUND),
    "Leek":                ([8, 9, 10, 11, 12], [10, 11, 12, 1, 2], [3, 4, 5, 6, 7, 8], [9, 10, 11, 12], [11, 12, 1, 2], [3, 4, 5, 6, 7, 8]),
    "Shallot":             ([6, 7, 8], [6, 7, 8], [12, 1], [6, 7, 8], [6, 7, 8], [12, 1]),
    "Garlic":              (EMPTY, [6], [11, 12, 1], EMPTY, [6], [11, 12, 1]),
    "Elephant garlic":     (EMPTY, [5, 6, 7], [12, 1], EMPTY, [5, 6, 7], [12, 1]),

    # Group 3 — Legumes
    "Pea (dwarf)":         ([3, 4, 5, 6, 7, 8], [4, 5, 6, 7, 8, 9], [6, 7, 8, 9, 10, 11], [3, 4, 5, 6, 7, 8], [4, 5, 6, 7, 8, 9], [6, 7, 8, 9, 10, 11]),
    "Snow pea":            ([3, 4, 5, 6, 7, 8], [4, 5, 6, 7, 8, 9], [6, 7, 8, 9, 10, 11], [3, 4, 5, 6, 7, 8], [4, 5, 6, 7, 8, 9], [6, 7, 8, 9, 10, 11]),
    "Sugar snap pea":      ([3, 4, 5, 6, 7, 8], [4, 5, 6, 7, 8, 9], [6, 7, 8, 9, 10, 11], [3, 4, 5, 6, 7, 8], [4, 5, 6, 7, 8, 9], [6, 7, 8, 9, 10, 11]),
    "Broad bean":          ([3, 4, 5, 6, 7], [3, 4, 5, 6, 7], [9, 10, 11, 12], [3, 4, 5, 6, 7], [3, 4, 5, 6, 7], [9, 10, 11, 12]),
    "Green bean (bush)":   ([9, 10, 11, 12, 1, 2], [10, 11, 12, 1, 2], [12, 1, 2, 3, 4, 5], [10, 11, 12, 1, 2], [10, 11, 12, 1, 2], [1, 2, 3, 4, 5]),
    "Climbing bean":       ([9, 10, 11, 12, 1], [10, 11, 12, 1, 2], [12, 1, 2, 3, 4, 5], [10, 11, 12, 1], [10, 11, 12, 1], [1, 2, 3, 4, 5]),
    "Runner bean":         ([9, 10, 11, 12, 1], [10, 11, 12, 1, 2], [12, 1, 2, 3, 4, 5], [10, 11, 12, 1], [10, 11, 12, 1], [1, 2, 3, 4, 5]),
    "Edamame":             ([10, 11, 12, 1], [11, 12, 1, 2], [2, 3, 4], [11, 12, 1], [11, 12, 1], [2, 3, 4]),

    # Group 9 — Other
    "Sweetcorn":           ([9, 10, 11, 12], [10, 11, 12, 1], [1, 2, 3, 4], [10, 11, 12, 1], [10, 11, 12, 1], [1, 2, 3, 4]),
    "Celery":              ([8, 9, 10], [9, 10, 11], [2, 3, 4, 5], [9, 10], [10, 11], [3, 4, 5]),
    "Florence fennel":     ([2, 3, 4, 8, 9], [2, 3, 4, 8, 9], [5, 6, 7, 11, 12], [3, 4, 5, 9], [3, 4, 5, 9], [6, 7, 12]),
    "Rhubarb":             ([6, 7, 8], [6, 7, 8], [9, 10, 11, 12, 1], [6, 7, 8], [6, 7, 8], [9, 10, 11, 12, 1]),
    "Asparagus":           ([6, 7, 8], [6, 7, 8], [9, 10, 11, 12], [6, 7, 8], [6, 7, 8], [9, 10, 11, 12]),
    "Globe artichoke":     ([8, 9, 10], [9, 10, 11], [10, 11, 12, 1], [9, 10], [10, 11], [10, 11, 12, 1]),
    "Ginger":              (EMPTY, [9, 10, 11], [6, 7, 8], EMPTY, EMPTY, EMPTY),
    "Turmeric":            (EMPTY, [9, 10, 11], [6, 7, 8], EMPTY, EMPTY, EMPTY),

    # Group 10 — Strawberry
    "Strawberry":          ([5, 6, 7, 8], [5, 6, 7, 8], [10, 11, 12, 1, 2, 3], [5, 6, 7, 8], [5, 6, 7, 8], [10, 11, 12, 1]),

    # Group 11 — Dwarf fruit (citrus: plant Sep–Nov best)
    "Meyer lemon (dwarf)":           (EMPTY, [9, 10, 11], [5, 6, 7, 8, 9], EMPTY, [9, 10, 11], [5, 6, 7, 8, 9]),
    "Tahitian lime (dwarf)":         (EMPTY, [9, 10, 11], [3, 4, 5, 6], EMPTY, [9, 10, 11], [3, 4, 5, 6]),
    "Kaffir lime":                   (EMPTY, [9, 10, 11], YEAR_ROUND, EMPTY, [9, 10, 11], YEAR_ROUND),
    "Kumquat Meiwa":                 (EMPTY, [9, 10, 11], [6, 7, 8, 9], EMPTY, [9, 10, 11], [6, 7, 8, 9]),
    "Calamondin orange":             (EMPTY, [9, 10, 11], YEAR_ROUND, EMPTY, [9, 10, 11], YEAR_ROUND),
    "Satsuma mandarin Miho":         (EMPTY, [9, 10, 11], [5, 6, 7], EMPTY, [9, 10, 11], [5, 6, 7]),
    "Clementine mandarin":           (EMPTY, [9, 10, 11], [6, 7, 8], EMPTY, [9, 10, 11], [6, 7, 8]),
    "Washington navel (dwarf)":      (EMPTY, [9, 10, 11], [7, 8, 9, 10], EMPTY, [9, 10, 11], [7, 8, 9, 10]),
    "Grapefruit (dwarf)":            (EMPTY, [9, 10, 11], [6, 7, 8, 9, 10], EMPTY, [9, 10, 11], [6, 7, 8, 9, 10]),
    "Yuzu":                          (EMPTY, [9, 10, 11], [5, 6, 7], EMPTY, [9, 10, 11], [5, 6, 7]),
    "Fig 'Brown Turkey'":            (EMPTY, [6, 7, 8], [1, 2, 3, 4, 6, 7], EMPTY, [6, 7, 8], [1, 2, 3, 4, 6, 7]),
    "Fig 'Petite Negra'":            (EMPTY, [6, 7, 8], [1, 2, 3, 4], EMPTY, [6, 7, 8], [1, 2, 3, 4]),
    "Peach 'Bonanza' (dwarf)":       (EMPTY, [6, 7, 8], [12, 1, 2], EMPTY, [6, 7, 8], [12, 1, 2]),
    "Peach 'Honey Babe' (dwarf)":    (EMPTY, [6, 7, 8], [12, 1, 2], EMPTY, [6, 7, 8], [12, 1, 2]),
    "Peach 'Pixzee' (dwarf)":        (EMPTY, [6, 7, 8], [12, 1, 2], EMPTY, [6, 7, 8], [12, 1, 2]),
    "Nectarine 'Flavourzee'":        (EMPTY, [6, 7, 8], [12, 1, 2], EMPTY, [6, 7, 8], [12, 1, 2]),
    "Nectarine 'Nectar Babe'":       (EMPTY, [6, 7, 8], [12, 1, 2], EMPTY, [6, 7, 8], [12, 1, 2]),
    "Apricot 'Garden Annie'":        (EMPTY, [6, 7, 8], [12, 1, 2], EMPTY, [6, 7, 8], [12, 1, 2]),
    "Cherry 'Griotella' (dwarf)":    (EMPTY, [6, 7, 8], [12, 1, 2], EMPTY, [6, 7, 8], [12, 1, 2]),
    "Plum (dwarf Santa Rosa)":       (EMPTY, [6, 7, 8], [12, 1, 2], EMPTY, [6, 7, 8], [12, 1, 2]),
    "Almond 'Garden Prince'":        (EMPTY, [6, 7, 8], [2, 3], EMPTY, [6, 7, 8], [2, 3]),
    "Apple 'Blush Babe' (dwarf)":    (EMPTY, [6, 7, 8], [2, 3, 4, 5], EMPTY, [6, 7, 8], [2, 3, 4, 5]),
    "Apple 'Thumbelina' Candy Crunch": (EMPTY, [6, 7, 8], [2, 3, 4, 5], EMPTY, [6, 7, 8], [2, 3, 4, 5]),
    "Mulberry 'Black English'":      (EMPTY, [6, 7, 8], [12, 1], EMPTY, [6, 7, 8], [12, 1]),
    "Pomegranate dwarf 'Nana'":      (EMPTY, [9, 10, 11], [4, 5, 6], EMPTY, [9, 10, 11], [4, 5, 6]),
    "Feijoa 'Bambina' (dwarf)":      (EMPTY, [4, 5, 6, 7, 8, 9, 10], [4, 5, 6], EMPTY, [4, 5, 6, 7, 8, 9, 10], [4, 5, 6]),
    "Guava 'Hawaiian' (dwarf)":      (EMPTY, [9, 10, 11], [3, 4, 5], EMPTY, [9, 10, 11], [3, 4, 5]),
    "Olive (potted)":                (EMPTY, [9, 10, 11], [5, 6, 7], EMPTY, [9, 10, 11], [5, 6, 7]),
    "Avocado 'Wurtz'":               (EMPTY, [9, 10, 11], [4, 5, 6, 7, 8, 9, 10], EMPTY, [9, 10, 11], [4, 5, 6, 7, 8, 9, 10]),
    "Macadamia (container young)":   (EMPTY, [9, 10, 11], [3, 4, 5, 6], EMPTY, [9, 10, 11], [3, 4, 5, 6]),
    "Tamarillo":                     (EMPTY, [9, 10, 11], [4, 5, 6, 7, 8, 9], EMPTY, [9, 10, 11], [4, 5, 6, 7, 8, 9]),
    "Dwarf banana 'Cavendish'":      (EMPTY, [9, 10, 11, 12], [1, 2, 3, 4], EMPTY, [9, 10, 11, 12], [1, 2, 3, 4]),
    "Coffee arabica":                (EMPTY, [9, 10, 11, 12], [8, 9, 10], EMPTY, [9, 10, 11, 12], [8, 9, 10]),

    # Group 12 — Berries / vines
    "Blueberry dwarf 'Top Hat'":         (EMPTY, [6, 7, 8], [12, 1, 2], EMPTY, [6, 7, 8], [12, 1, 2]),
    "Raspberry 'Raspberry Shortcake'":   (EMPTY, [6, 7, 8], [12, 1, 2], EMPTY, [6, 7, 8], [12, 1, 2]),
    "Blackberry (thornless)":            (EMPTY, [6, 7, 8], [1, 2, 3], EMPTY, [6, 7, 8], [1, 2, 3]),
    "Boysenberry":                       (EMPTY, [6, 7, 8], [1, 2, 3], EMPTY, [6, 7, 8], [1, 2, 3]),
    "Loganberry":                        (EMPTY, [6, 7, 8], [1, 2, 3], EMPTY, [6, 7, 8], [1, 2, 3]),
    "Gooseberry":                        (EMPTY, [6, 7, 8], [12, 1], EMPTY, [6, 7, 8], [12, 1]),
    "Redcurrant":                        (EMPTY, [6, 7, 8], [12, 1], EMPTY, [6, 7, 8], [12, 1]),
    "Blackcurrant":                      (EMPTY, [6, 7, 8], [12, 1], EMPTY, [6, 7, 8], [12, 1]),
    "Cranberry":                         (EMPTY, [6, 7, 8], [4, 5, 6], EMPTY, [6, 7, 8], [4, 5, 6]),
    "Goji berry":                        (EMPTY, [9, 10, 11], [2, 3, 4, 5], EMPTY, [9, 10, 11], [2, 3, 4, 5]),
    "Passionfruit 'Black'":              (EMPTY, [9, 10, 11], [2, 3, 4, 5, 6], EMPTY, [9, 10, 11], [2, 3, 4, 5, 6]),
    "Passionfruit 'Panama Gold'":        (EMPTY, [9, 10, 11], [2, 3, 4, 5, 6], EMPTY, [9, 10, 11], [2, 3, 4, 5, 6]),
    "Kiwifruit Hayward + Tomuri":        (EMPTY, [6, 7, 8], [4, 5, 6], EMPTY, [6, 7, 8], [4, 5, 6]),
    "Grape (table, 'Niagara')":          (EMPTY, [6, 7, 8], [2, 3, 4], EMPTY, [6, 7, 8], [2, 3, 4]),
    "Dragon fruit":                      (EMPTY, [9, 10, 11], [3, 4, 5], EMPTY, [11, 12], [3, 4, 5]),
    "Pineapple":                         (EMPTY, [9, 10, 11], [12, 1, 2, 3], EMPTY, [11, 12], [1, 2, 3]),
}

# Crops that have no clear research mapping (default fallback applied)
UNMATCHED = {"Horseradish"}


def to_csv(months: list[int]) -> str:
    return ",".join(str(m) for m in months)


def main() -> int:
    with JSON_PATH.open("r", encoding="utf-8") as f:
        crops = json.load(f)

    updated = 0
    missing = []
    changed_names = []
    for c in crops:
        name = c["commonName"]
        if name not in DATES:
            missing.append(name)
            continue
        gh_sow, gh_plant, gh_harvest, out_sow, out_plant, out_harvest = DATES[name]
        before = (
            c.get("seedSowMonths"), c.get("seedlingPlantMonths"), c.get("harvestMonths"),
            c.get("outdoorSowMonths"), c.get("outdoorSeedlingMonths"), c.get("outdoorHarvestMonths"),
        )
        c["seedSowMonths"] = list(gh_sow)
        c["seedlingPlantMonths"] = list(gh_plant)
        c["harvestMonths"] = list(gh_harvest)
        c["outdoorSowMonths"] = list(out_sow)
        c["outdoorSeedlingMonths"] = list(out_plant)
        c["outdoorHarvestMonths"] = list(out_harvest)
        after = (gh_sow, gh_plant, gh_harvest, out_sow, out_plant, out_harvest)
        if before != after:
            updated += 1
            changed_names.append(name)

    # Write JSON back as a single-line-per-crop array (preserve existing style: one crop per line)
    lines = ["["]
    for i, c in enumerate(crops):
        sep = "," if i < len(crops) - 1 else ""
        lines.append(json.dumps(c, ensure_ascii=False, separators=(",", ":")) + sep)
    lines.append("]")
    JSON_PATH.write_text("\n".join(lines) + "\n", encoding="utf-8")

    # Generate Migration6to7.kt — one upd() call per crop in DATES (covers all 153)
    kt = []
    kt.append("package nz.keeleysgreenhouse.app.data.migrations")
    kt.append("")
    kt.append("import androidx.room.migration.Migration")
    kt.append("import androidx.sqlite.db.SupportSQLiteDatabase")
    kt.append("")
    kt.append("/**")
    kt.append(" * Replaces sow / plant / harvest months for every seeded crop with research-")
    kt.append(" * backed Bay-of-Plenty (Papamoa) data drawn from")
    kt.append(" * docs/PAPAMOA_CROP_DATA_RESEARCH.md (Tui, Daltons, Kings Seeds NZ, Troppo BOP,")
    kt.append(" * Mitre 10, The Greenhouse Co NZ). Data-only migration — schema is unchanged.")
    kt.append(" */")
    kt.append("val MIGRATION_6_7 = object : Migration(6, 7) {")
    kt.append("    override fun migrate(db: SupportSQLiteDatabase) {")
    kt.append("        fun upd(")
    kt.append("            name: String,")
    kt.append("            sow: String,")
    kt.append("            plant: String,")
    kt.append("            harvest: String,")
    kt.append("            outSow: String,")
    kt.append("            outPlant: String,")
    kt.append("            outHarvest: String")
    kt.append("        ) {")
    kt.append("            db.execSQL(")
    kt.append("                \"\"\"")
    kt.append("                UPDATE crops SET")
    kt.append("                  seedSowMonths = ?,")
    kt.append("                  seedlingPlantMonths = ?,")
    kt.append("                  harvestMonths = ?,")
    kt.append("                  outdoorSowMonths = ?,")
    kt.append("                  outdoorSeedlingMonths = ?,")
    kt.append("                  outdoorHarvestMonths = ?")
    kt.append("                WHERE commonName = ?")
    kt.append("                \"\"\".trimIndent(),")
    kt.append("                arrayOf(sow, plant, harvest, outSow, outPlant, outHarvest, name)")
    kt.append("            )")
    kt.append("        }")
    kt.append("")
    for c in crops:
        name = c["commonName"]
        if name not in DATES:
            continue
        gh_sow, gh_plant, gh_harvest, out_sow, out_plant, out_harvest = DATES[name]
        esc = name.replace("\\", "\\\\").replace("\"", "\\\"")
        kt.append(
            f'        upd("{esc}", '
            f'"{to_csv(gh_sow)}", "{to_csv(gh_plant)}", "{to_csv(gh_harvest)}", '
            f'"{to_csv(out_sow)}", "{to_csv(out_plant)}", "{to_csv(out_harvest)}")'
        )
    kt.append("    }")
    kt.append("}")

    MIGRATION_PATH.write_text("\n".join(kt) + "\n", encoding="utf-8")

    print(f"updated={updated} missing={len(missing)} unmatched_default={len(UNMATCHED)}")
    if missing:
        print("MISSING from DATES dict:")
        for n in missing:
            print(f"  - {n}")
    print(f"unmatched (used best-guess defaults): {sorted(UNMATCHED)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
