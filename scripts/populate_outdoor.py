"""Populate outdoor* fields on crops.json per Papamoa rules.

Rules:
- Warm-season fruiting/cucurbit/heat (FRUITING, CUCURBIT incl. tomato/capsicum/
  chilli/eggplant/melons/okra/tomatillo/zucchini/cucumber/beans):
    outdoorSowMonths       = greenhouse seedSowMonths shifted +6 weeks (~+1.5 months)
    outdoorSeedlingMonths  = months in seedlingPlantMonths intersected with [10,11,12]
                             (post-frost transplant window) — fall back to [10,11,12]
                             if intersection empty
    outdoorHarvestMonths   = harvestMonths shifted +1 month (later outdoor)
- Cool-season (BRASSICA, LEAFY, LEGUME, ALLIUM):
    outdoor* = same as greenhouse (or +2 weeks). We mirror greenhouse months.
- Direct-sow ROOT (carrot, radish, parsnip, beetroot, etc.):
    outdoorSowMonths       = same as greenhouse seedSowMonths
    outdoorSeedlingMonths  = []   (direct-sown)
    outdoorHarvestMonths   = same as greenhouse harvestMonths
- HERB / MICROGREEN:
    mirror greenhouse months.
- FRUIT_TREE / VINE_BERRY / PERENNIAL:
    outdoorSowMonths = []  (already grown outdoors as standard)

outdoorNotes: one Papamoa-flavoured sentence per non-tree crop.
"""
import json
import re
from pathlib import Path

PATH = Path(r'C:/projects/greenhouse/app/src/main/assets/seed/crops.json')

WARM_HEAT_NAMES = {
    'tomato', 'cherry tomato', 'beefsteak tomato', 'roma tomato', 'capsicum',
    'chilli', 'eggplant', 'tomatillo', 'cape gooseberry', 'pepino', 'okra',
    'cucumber', 'gherkin', 'zucchini', 'rockmelon', 'watermelon',
    'honeydew melon', 'spaghetti squash', 'marrow', 'luffa',
    'pumpkin (crown)', 'buttercup squash', 'butternut',
    'green bean', 'climbing bean', 'runner bean', 'edamame', 'sweetcorn',
    'basil', 'thai basil', 'amaranth', 'kūmara', 'kumara',
}

GENERIC_NOTES = {
    'FRUITING': "Wait until late October to plant out — Papamoa coastal nights need to settle above 12 °C and the salt wind ridge eases. Stake well, mulch deeply.",
    'CUCURBIT': "Direct-sow or transplant after Labour Weekend once soil is reliably warm; train on a sturdy frame to keep fruit off humid ground.",
    'HERB': "Tolerates Papamoa summers in a sheltered, free-draining bed; pinch tips weekly and water deeply twice a week in dry spells.",
    'LEAFY': "Grows year-round outdoors here; provide afternoon shade Dec–Feb to slow bolting and keep soil consistently moist.",
    'BRASSICA': "Best as a cool-season outdoor crop Mar–Sep; net against white butterfly and watch for slugs after Bay of Plenty rain.",
    'ROOT': "Direct-sow into deep, stone-free soil; thin early and water steadily to avoid forking and splitting.",
    'ALLIUM': "Plants happily outdoors year-round in Papamoa's mild coastal soil; keep weed-free and reduce watering as bulbs swell.",
    'LEGUME': "Sow direct into warm soil with sturdy support; mulch to hold moisture and keep humidity off the lower foliage.",
    'MICROGREEN': "Grow trays on a sheltered porch March–November; bring indoors in peak summer to avoid heat stress.",
    'PERENNIAL': "Establish in a sheltered, free-draining outdoor bed; mulch annually and divide every few years to keep vigour.",
    'FRUIT_TREE': "",
    'VINE_BERRY': "",
}

SPECIFIC_NOTES = {
    'tomato': "Move outdoors only after Labour Weekend once Papamoa nights stay above 12 °C; stake every plant and prune laterals weekly to keep airflow in coastal humidity.",
    'capsicum': "Outdoor plants need a hot, sheltered north-facing wall in Papamoa; harvest takes 2–3 weeks longer outside than under cover.",
    'chilli': "Papamoa summers are usually warm enough outdoors against a sheltered wall — but expect lighter yields than greenhouse-grown.",
    'eggplant': "Outdoor success is marginal in Papamoa — choose your warmest, most sheltered spot and plant only after late October.",
    'tomatillo': "Plant at least two outdoors for cross-pollination; needs the warmest spot you have and steady summer water.",
    'okra': "Outdoor okra is borderline in Papamoa — only attempt against a hot north wall after late October, and expect half the yield of greenhouse plants.",
    'cucumber': "Trellis vertically outdoors to keep fruit off humid ground; mulch deeply and water at the base to reduce mildew.",
    'zucchini': "Direct-sow outdoors after Labour Weekend; leave 1 m between plants for airflow against Bay of Plenty mildew pressure.",
    'rockmelon': "Outdoor melons need full sun, free-draining soil and a long warm autumn — best on a black weed-mat to lift soil temperatures.",
    'watermelon': "Outdoor success in Papamoa needs a hot, sheltered, north-facing slope; small icebox varieties ripen most reliably.",
    'honeydew melon': "Outdoors only on the hottest, most sheltered spot you have; lift fruit onto straw or a tile to ripen.",
    'basil': "Plant outdoors against a north-facing wall after Labour Weekend; pinch flowers as they appear and harvest before the first cool snap.",
    'lettuce': "Sow successively outdoors year-round; provide afternoon shade Dec–Feb and pick early morning to avoid wilting.",
    'spinach': "Bolts in Papamoa heat — best as an autumn-to-spring outdoor crop in a part-shaded bed.",
    'silverbeet': "Reliable outdoor crop year-round in Papamoa; pick outer leaves regularly to keep the plant productive.",
    'kale': "Best outdoors as a cool-season crop Mar–Sep; net against white butterfly and harvest from the bottom up.",
    'broccoli': "Time outdoor crops to head up in cool weather; protect from white butterfly with fine mesh from transplant.",
    'cauliflower': "Needs steady cool weather and consistent water — best as an outdoor autumn-to-winter crop in Papamoa.",
    'cabbage (round + savoy)': "Reliable outdoor crop year-round if netted against white butterfly and watered consistently.",
    'carrot': "Direct-sow outdoors year-round into deep, stone-free soil; keep evenly moist for the first three weeks to ensure germination.",
    'radish': "Quickest outdoor crop in Papamoa — sow every 2–3 weeks and harvest before they get pithy.",
    'beetroot': "Direct-sow outdoors year-round in Papamoa; thin early to give roots space to swell.",
    'parsnip': "Slow to germinate — sow direct into deep, stone-free soil and keep evenly moist for 2–3 weeks before thinning.",
    'pea (dwarf)': "Sow outdoors Mar–Sep; the cooler shoulder seasons in Papamoa give the sweetest pods.",
    'snow pea': "Outdoor cool-season crop Mar–Sep in Papamoa; pick every 2–3 days to keep pods tender.",
    'broad bean': "Direct-sow outdoors Mar–Jul; tall varieties need staking against Papamoa's spring sea breeze.",
    'green bean (bush)': "Direct-sow into warm soil from late October; pick young and often to keep plants producing into autumn.",
    'climbing bean': "Build a sturdy frame against the prevailing wind; sow after Labour Weekend and harvest right through to April.",
    'sweetcorn': "Block-plant 4×4 outdoors after Labour Weekend for reliable wind pollination; needs deep, rich, well-watered soil.",
    'kūmara': "Plant slips into warm, free-draining mounds Oct–Dec; harvest before the first autumn cool snap.",
    'garlic': "Plant cloves outdoors on the shortest day (mid-June) and harvest when the lower leaves yellow in late November.",
    'onion (long-day NZ)': "Sow seed Mar–Jul outdoors and transplant in winter; harvest when tops fall over and cure in the sun.",
    'spring onion': "Reliable outdoor crop year-round in Papamoa; sow every few weeks for a continuous supply.",
    'leek': "Plant deep into a trench from October and earth up as they grow for long, white shanks.",
    'potato': "Plant chitted seed potatoes outdoors Aug–Dec; mound soil over shoots to prevent green tubers and protect from late frost.",
    'rocket': "Sow outdoors Mar–Oct; bolts quickly in summer heat — give afternoon shade and pick young.",
    'mesclun mix': "Sow outdoors year-round in a sheltered, part-shaded bed; cut at 8–10 cm and let regrow twice.",
    'coriander': "Bolts fast in Papamoa heat — best as a cool-season outdoor crop in autumn and spring; sow direct, don't transplant.",
    'parsley (flat + curly)': "Reliable outdoor herb year-round in Papamoa; mulch in summer to keep roots cool.",
    'mint': "Always plant in a pot or buried bottomless container outdoors — mint will overrun a Papamoa garden bed in one season.",
}


def shift_months(months, offset):
    return [((m - 1 + offset) % 12) + 1 for m in months]


def populate(crop):
    cat = crop['category']
    name = crop['commonName'].lower()
    sow = crop.get('seedSowMonths', [])
    seedling = crop.get('seedlingPlantMonths', [])
    harvest = crop.get('harvestMonths', [])

    if cat in ('FRUIT_TREE', 'VINE_BERRY'):
        crop['outdoorSowMonths'] = []
        crop['outdoorSeedlingMonths'] = []
        crop['outdoorHarvestMonths'] = []
        crop['outdoorNotes'] = ""
        return

    if cat == 'PERENNIAL':
        crop['outdoorSowMonths'] = sow[:]
        crop['outdoorSeedlingMonths'] = seedling[:]
        crop['outdoorHarvestMonths'] = harvest[:]
        crop['outdoorNotes'] = SPECIFIC_NOTES.get(name, GENERIC_NOTES['PERENNIAL'])
        return

    if cat in ('FRUITING', 'CUCURBIT') or any(k in name for k in ('bean', 'corn', 'basil', 'amaranth', 'kūmara', 'kumara')):
        outdoor_sow = shift_months(sow, 2) if sow else []
        # restrict to spring/summer outdoor window for warm crops: Sep..Feb
        warm_window = {9, 10, 11, 12, 1, 2}
        outdoor_sow = [m for m in outdoor_sow if m in warm_window] or outdoor_sow
        if seedling:
            seed_filtered = [m for m in seedling if m in {10, 11, 12, 1}]
            outdoor_seedling = seed_filtered or [10, 11, 12]
        else:
            outdoor_seedling = []
        outdoor_harvest = shift_months(harvest, 1) if harvest else []
        crop['outdoorSowMonths'] = outdoor_sow
        crop['outdoorSeedlingMonths'] = outdoor_seedling
        crop['outdoorHarvestMonths'] = outdoor_harvest
        note = SPECIFIC_NOTES.get(name)
        if not note:
            note = GENERIC_NOTES.get(cat, GENERIC_NOTES['FRUITING'])
        crop['outdoorNotes'] = note
        return

    if cat == 'ROOT':
        crop['outdoorSowMonths'] = sow[:]
        crop['outdoorSeedlingMonths'] = []   # direct-sown
        crop['outdoorHarvestMonths'] = harvest[:]
        crop['outdoorNotes'] = SPECIFIC_NOTES.get(name, GENERIC_NOTES['ROOT'])
        return

    # Cool-season + herb + leafy + brassica + allium + legume + microgreen: mirror
    crop['outdoorSowMonths'] = sow[:]
    crop['outdoorSeedlingMonths'] = seedling[:]
    crop['outdoorHarvestMonths'] = harvest[:]
    crop['outdoorNotes'] = SPECIFIC_NOTES.get(name, GENERIC_NOTES.get(cat, ""))


def main():
    data = json.loads(PATH.read_text(encoding='utf-8'))
    for crop in data:
        populate(crop)
    # Pretty-print preserving structure: one crop per line block
    out = json.dumps(data, ensure_ascii=False, indent=None, separators=(',', ':'))
    # Reformat: each crop on its own line, wrapped in array brackets like the original
    lines = ['[']
    for i, c in enumerate(data):
        suffix = ',' if i < len(data) - 1 else ''
        lines.append(json.dumps(c, ensure_ascii=False, separators=(',', ':')) + suffix)
    lines.append(']')
    PATH.write_text('\n'.join(lines) + '\n', encoding='utf-8')
    print(f"Updated {len(data)} crops.")


if __name__ == '__main__':
    main()
