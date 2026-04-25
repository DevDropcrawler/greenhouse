# Keeley's Greenhouse (Papamoa) — Android App Build Plan (v3)

> **Purpose:** one plan you paste into Claude Code. v3 adds: app renamed to **Keeley's Greenhouse**, bold olive app-bar signature (inverted in dark), full motion spec, expanded screen list (57 views), and a full single-pass superdesign.dev prompt.

---

## 1. Project brief

- **Name:** Keeley's Greenhouse
- **Platform:** Android only, min SDK 26 (Android 8.0), target SDK 34
- **Audience:** home greenhouse growers in Papamoa / Bay of Plenty
- **Offline-first:** all crop, pest, and image data ship in the APK. YouTube links use the user's installed YouTube app via intents (no API key, no in-app network code).
- **No login, no cloud, no accounts.** Room (SQLite) only.
- **Core value:** tell me what to sow this month (seed or seedling), for my greenhouse in this climate, with the pests to watch, and short videos for every crop and pest.

---

## 2. Papamoa / Bay of Plenty climate facts

Köppen: **Cfb** (temperate oceanic). In NZ gardening terms this is the **warm / northern planting zone**.

| Metric | Value |
|---|---|
| Annual mean temp | 14.6–15.5 °C |
| Warmest month | Feb, avg 19.1 °C |
| Coolest month | Jul, avg 10.3 °C |
| Annual rainfall | ~1358 mm |
| Wettest | Jun (~137 mm) |
| Driest | Jan (~87 mm) |
| Peak sunshine | Jan, ~262 hrs |
| Frost | rare on coast; inland BOP gets occasional light frost May–Sep. Last frost typically by early Oct. |
| Humidity | moderate-high year-round; high disease pressure in summer |

Greenhouse implications for Papamoa:
- **Ventilation + shade cloth Dec–Feb** (heat stress, flower drop >30 °C)
- **Humidity management May–Aug** (botrytis risk >80 % RH)
- **Wind shelter** (coastal)
- Extends warm-crop season by ~6–8 weeks each side

Target greenhouse conditions (reference tile in-app):

| Crop group | Day | Night | RH |
|---|---|---|---|
| Tomato | 20–25 °C | 16–18 °C | 60–70 % |
| Capsicum / chilli | 22–28 °C | 18–20 °C | 65–75 % |
| Cucumber / melon | 24–28 °C | 18–20 °C | 70–80 % |
| Brassica / leafy | 15–20 °C | 10–15 °C | 50–70 % |
| Dwarf citrus (pot) | 18–28 °C | 10–15 °C | 50–70 % |
| Stone fruit | outdoor in winter for chill | — | — |

---

## 3. Tech stack (locked)

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3 (custom theme — see §9)
- **DB:** Room (SQLite), seeded from bundled JSON on first launch
- **Images:** bundled WebP in `res/drawable-nodpi/`, Coil for loading
- **Nav:** Navigation Compose, single Activity
- **Architecture:** MVVM. Packages: `data/`, `domain/`, `ui/`, `di/`, `util/`
- **DI:** Hilt
- **Search:** Room FTS4 across crops, aliases, Māori names, pests, diseases
- **Video:** `Intent.ACTION_VIEW` with `vnd.youtube:VIDEO_ID` (opens YouTube app) or `https://www.youtube.com/results?search_query=...` (browser fallback). Seed JSON holds `youtubeVideoIds: List<String>` + `youtubeSearchQuery: String`.
- **Build:** Gradle Kotlin DSL, version catalog
- **Permissions:** `INTERNET` only if we want to show YouTube thumbnails (see §6). Otherwise none.

No Firebase. No analytics.

---

## 4. Data model

### 4.1 Room entities

```
Crop
  id, commonName, maoriName?, aliases: List<String>,
  family, category: Enum(FRUITING,LEAFY,ROOT,BRASSICA,LEGUME,
                         ALLIUM,HERB,CUCURBIT,FRUIT_TREE,
                         VINE_BERRY,PERENNIAL,MICROGREEN),
  isFruitTree: Boolean,
  greenhouseRecommended: Boolean,
  outdoorAlsoOk: Boolean,
  seedSowMonths: List<Int>,          // 1..12, warm zone under glass
  seedlingPlantMonths: List<Int>,
  harvestMonths: List<Int>,
  daysSeedToSeedling: IntRange,
  daysSeedlingToHarvest: IntRange,
  yearsToFirstFruit: Int?,           // fruit trees
  sowDepthMm: Int?,
  spacingCm: Int,
  rowSpacingCm: Int?,
  potSizeLitres: Int?,               // fruit trees / container crops
  dayTempC: IntRange,
  nightTempC: IntRange,
  humidityPct: IntRange,
  sunNeed: Enum(FULL,PARTIAL),
  soilPhRange: ClosedFloatingPointRange<Float>,
  waterNeed: Enum(LOW,MEDIUM,HIGH),
  feedingNotes: String,              // ≤3 sentences
  sowingNotes: String,
  greenhouseNotes: String,           // BOP-specific
  pruningNotes: String?,
  pollinationNotes: String?,
  companionPlants: List<Int>,
  avoidPlants: List<Int>,
  commonPests: List<Int>,
  commonDiseases: List<Int>,
  imageResName: String,
  thumbnailResName: String,
  youtubeVideoIds: List<String>,     // 0–3 curated IDs
  youtubeSearchQuery: String         // fallback, always populated

Pest
  id, name, aliases, shortDescription, symptoms,
  pestImageResName, damageImageResName,
  organicControls: List<String>,
  biologicalControls: List<String>,  // NZ-available (Bioforce etc.)
  chemicalNotes: String,
  preventionTips: List<String>,
  affectedCrops: List<Int>,
  youtubeVideoIds, youtubeSearchQuery

Disease
  id, name, description, symptoms, conditions,
  controls: List<String>, imageResName,
  affectedCrops: List<Int>,
  youtubeVideoIds, youtubeSearchQuery

UserPlanting
  id, cropId, sownDate, transplantedDate?,
  expectedHarvestStart, expectedHarvestEnd,
  location, notes, photoUri?

Task
  id, plantingId?, cropId?, dueDate, title, done

Favourite
  id, refType(CROP|PEST|DISEASE), refId, savedAt
```

### 4.2 FTS4

```
CropFts(cropId, commonName, maoriName, aliases, family, pestNames, diseaseNames)
PestFts(pestId, name, aliases)
DiseaseFts(diseaseId, name)
```

---

## 5. Features (v1 scope)

> **Note:** v1 is **light theme only**. Dark mode is post-v1. The design tokens in §9 still document dark variants for future use, but no dark-theme code or screens are built in v1.

1. **Home / This Month** — sow-from-seed / transplant / harvest lists, today's tasks, shortcut to search and calendar.
2. **Calendar** — 12-month grid with count badges; month detail tabs (Seed / Seedling / Harvest).
3. **Crop list + detail** — filter chips by category; detail shows hero image, 12-month timeline, facts grid, expandable sections (Sowing, Greenhouse, Pruning, Pollination, Companions, Pests, Diseases, Videos).
4. **Pest + Disease guide** — list, detail, cross-links to affected crops.
5. **My garden** — log plantings, auto-generate transplant + harvest reminders, progress bars.
6. **Search** — one box hits crops, pests, diseases, aliases, Māori names.
7. **Videos** — on crop/pest/disease detail, a "Videos" section shows curated videos as cards; tap opens YouTube app via intent. "More on YouTube" opens search intent.
8. **Settings** — notification time, dark mode, reset data, about/credits.

Out of scope v1: cloud sync, weather API, multi-user, sharing, YouTube inline player.

---

## 6. YouTube integration — detail

**Why intents, not YouTube Data API:** no API key, no key rotation, no rate limits, minimal permissions. The YouTube app (or browser) does the fetching.

**Seed data per crop/pest/disease:**
- `youtubeVideoIds`: 0–3 curated IDs you've watched and approved (see §11.3 channels).
- `youtubeSearchQuery`: always populated, e.g. `"growing tomatoes greenhouse new zealand"`.

**In-app behaviour:**
- Video section shows curated-ID cards first. Each card stores `videoId`, `title`, `durationText`, `channel` in the seed JSON — **we do not scrape YouTube**.
- Tap card → `Intent.ACTION_VIEW` with `Uri.parse("vnd.youtube:$videoId")`. If YouTube app isn't installed, fall back to `https://www.youtube.com/watch?v=$videoId`.
- "More videos" → `Intent.ACTION_VIEW` with `https://www.youtube.com/results?search_query=${encoded query}`.
- Thumbnails: load from `https://img.youtube.com/vi/$videoId/mqdefault.jpg` when online, else show a local `video_placeholder.webp` with the play icon and title. **Decision:** add `INTERNET` permission only for thumbnails; document it explicitly in `AndroidManifest.xml` with a comment.

**Curation workflow (one-off task):**
- For each crop/pest/disease, find 1–3 short (≤15 min) videos from the allow-listed channels in §11.3.
- Store `videoId`, `title`, `durationText`, `channel` in `seed/videos.json`.
- Re-check links every 12 months.

---

## 7. Build phases — one Claude Code session each

> **Rule: one phase = one session. Commit `phase N: <n>` and stop.**

| # | Phase | What ships |
|---|---|---|
| 0 | Skeleton | Project, Hilt app, empty scaffold, version catalog |
| 1 | Data layer | Entities, DAOs, FTS, seeder, seed JSON loaded (all 150+ crops) |
| 2 | Home / This Month | 3 horizontal lists (sow/transplant/harvest), today's tasks strip |
| 3 | Crop list + detail | Grid + filter chips, detail with timeline bar, expandables |
| 4 | Calendar | 12-month grid + month detail tabs |
| 5 | Pest + Disease guide | Lists + detail + cross-links |
| 6 | Search | FTS-backed single box, recent searches, grouped results |
| 7 | My garden + tasks | Add planting flow, auto-tasks, daily 08:00 notification |
| 8 | Videos + polish | Video cards, YouTube intents, theme polish, settings, a11y pass, match superdesign mockups |

**Done per phase:** tests pass, app installs on real device, zero new lint errors, `docs/screenshots/phase-N.png` committed.

---

## 8. Claude Code workflow rules (pin these in every session)

1. **One phase per session.** Commit `phase N: <n>`. Stop at the boundary.
2. **Re-read this file before writing code each session.** Confirm active phase.
3. **No new libraries** without a one-line justification in the commit message.
4. **`./gradlew assembleDebug` must be green** after every change.
5. **Seed data lives in JSON.** Never hard-code crops in Kotlin.
6. **Images:** WebP in `res/drawable-nodpi/`, ≤200 KB each. Names: `crop_<snake>`, `pest_<snake>`, `disease_<snake>`, `thumb_<snake>`.
7. **Strings in `strings.xml`** — no hard-coded UI copy.
8. **`INTERNET` permission only for YouTube thumbnails.** No other network usage.
9. **Tests per phase:** DAO + seeder (1), VM happy paths (2–5, 7), FTS tests (6), task-gen (7), intent launch (8).
10. **If a refactor would cross phases, stop and ask.**
11. **Commit hygiene:** small commits, present-tense messages, no emojis.
12. **`./gradlew verifyArt` must pass** — every crop/pest/disease in seed JSON has matching drawables, else build fails.
13. **Design parity:** after superdesign.dev URL is supplied, Phase 8 compares every screen against the mockup and logs deltas in `docs/design-deltas.md`.
14. **Sticky bottom nav:** implement via `Scaffold(bottomBar = …)` with a `NavigationBar` that has `containerColor.copy(alpha = 0.92f)` + `Modifier.blur` backdrop. Every main-screen `LazyColumn` / `LazyVerticalGrid` uses `contentPadding = PaddingValues(bottom = 72.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())`. Detail screens use a `Scaffold` with `bottomBar` for the sticky action bar and no `NavigationBar`.
15. **Superdesign IDE extension** (optional, recommended from Phase 2 onward): install the Superdesign extension in Claude Code. Store the web export HTML in `.superdesign/baseline/` (git-tracked, read-only reference). From Phase 2, design tweaks are made in-IDE so Compose changes and design updates happen in the same session. No API key needed — uses Claude Code's LLM. Open canvas with `cmd+shift+p → superdesign: open canva`.

---

## 9. Visual design — one-pass prompt for superdesign.dev (v3)

> Paste the block below into a fresh superdesign.dev session. It produces 57 screens × light+dark = 114 frames plus 10 style-guide frames in one pass. Do not edit the old session — start new.

````
KEELEY'S GREENHOUSE — Android app design brief (single-pass, complete)

Generate a complete Android mobile app design system and ALL screens listed
below in ONE coherent design language. Do not generate placeholder screens,
duplicates, or "coming soon" states. Do not invent screens outside §9. Every
screen must be production-ready. Generate LIGHT + DARK variants for each.

══════════════════════════════════════════════════════════════════════
1. IDENTITY
══════════════════════════════════════════════════════════════════════
App name:  Keeley's Greenhouse
Tagline:   "Your Papamoa greenhouse, month by month."
Audience:  home greenhouse growers in Papamoa / Bay of Plenty, New Zealand.
Vibe:      modern botanical field journal meets calm productivity app.
           Premium, tactile, earthy. NOT generic Material 3. NOT techy.
Platform:  Android phone, 6.1" viewport (412 × 915 dp).

══════════════════════════════════════════════════════════════════════
2. COLOR SYSTEM (6 tokens + states)
══════════════════════════════════════════════════════════════════════
LIGHT
- Primary / Forest:       #2E5D3A   action, emphasis, active states
- Display / Olive Moss:   #6B7A3F   app bar fill + display headlines
- Secondary / Terracotta: #C06B3A   warmth, badges, secondary CTAs
- Tertiary / Honey:       #E0A458   harvest accent
- Metallic / Brass:       #B89968   finishing — hairlines, eyebrows, numerals
- Background / Cream:     #FAF7F1   surface
- On-surface text:        #1E2A22
- Success / Sprout:       #4E8C52
- Error:                  #B4442F

DARK
- Background:             #10140F
- Surface:                #1A1F17
- On-surface text:        #E8E5D7
- Forest (lifted):        #5E9E6A
- Olive Moss:             #8A9A5B
- Terracotta:             #D4825A
- Honey:                  #E8B77A
- Brass:                  #D4B57A

══════════════════════════════════════════════════════════════════════
3. APP BAR — BOLD SOLID FILL (new signature element)
══════════════════════════════════════════════════════════════════════
LIGHT: app bar is SOLID OLIVE MOSS (#6B7A3F) edge-to-edge.
- Text + icons on the bar are cream (#FAF7F1).
- Status bar matches olive.
- 56 dp tall. Bottom edge: 0.5 dp brass hairline. No shadow.

DARK: app bar INVERTS — SOLID CREAM (#E8E5D7).
- Text + icons on the bar are deep ink (#1E2A22).
- Status bar matches cream.
- Same 56 dp, same brass hairline.

Content: back arrow (detail only), title in Fraunces 20 semibold, trailing
icons — search + settings on main, share + favourite on detail. Greetings
("Good morning") live BELOW the app bar in body content, not on it.

══════════════════════════════════════════════════════════════════════
4. TYPOGRAPHY
══════════════════════════════════════════════════════════════════════
- Display + headings: Fraunces (variable optical size), semibold, slightly
  tight tracking. Italic for Latin/Māori subtitles.
- Body + UI: Inter 16/24 body, 14 captions, 12 eyebrows.
- Numbers: Inter Tabular.
- Eyebrow small-caps: Fraunces 11, brass, +10% tracking, leaf glyph prefix.
- Display titles in body: Olive Moss (light) / Olive dark variant.

══════════════════════════════════════════════════════════════════════
5. SHAPE, SHADOW, TEXTURE
══════════════════════════════════════════════════════════════════════
- Card radius 22 dp. Buttons full pills.
- Hero images: full-bleed on detail, top-rounded 24 dp on cards.
- Warm shadow: rgba(46,45,33,0.06), 2-layer (2 dp + 8 dp, y-offset 2 dp).
- 4% paper-grain texture on cream (light only).
- Body line-height 1.55.
- 1 dp brass botanical dividers between major sections.

══════════════════════════════════════════════════════════════════════
6. ICONOGRAPHY — custom, 1.5 dp stroke, rounded joins, botanical
══════════════════════════════════════════════════════════════════════
Home = pitched roof + chimney leaf
Calendar = 4-cell grid + leaf in first cell
Crops = sprout in pot
Garden = tipped watering can + water arc
More = 3 dots as fruit on a stem
Search = magnifier with leaf tail
Settings = gear with notched leaf
Bug = stylised whitefly silhouette
Plus: sun, droplet, thermometer, ruler, seed packet, trowel, secateurs,
calendar-pin, bell, share, favourite (heart with leaf), check, chevron.

══════════════════════════════════════════════════════════════════════
7. BOTTOM NAV (sticky, on main tabs only)
══════════════════════════════════════════════════════════════════════
- 72 dp. Translucent cream @ 92% with 8 dp backdrop blur (light).
  Dark: translucent #1A1F17 @ 92%.
- Top edge: 0.5 dp brass hairline — must render visibly.
- 5 tabs: Home, Calendar, Crops, Garden, More.
- Active: forest icon + label + 2 dp brass underline (24 dp wide, 8 dp below
  label, centered).
- Inactive: on-surface at 55%.
- Labels: Inter semibold 11 sp, 4 dp from icon. Respects safe-area inset.
- Detail screens hide bottom nav; show sticky bottom action bar instead.

══════════════════════════════════════════════════════════════════════
8. MOTION + ANIMATION (spec on every frame as dotted overlays)
══════════════════════════════════════════════════════════════════════
Global:
- Screen push: 250 ms slide-in right + fade (emphasized easing).
- Screen pop: 200 ms slide-out right + fade.
- Tab switch: 180 ms crossfade + 8 dp y-rise spring.
- Bottom-nav underline: slides between tabs, spring 700/28, ~220 ms.
- Card press: scale 0.97, 120 ms, haptic.
- Button press: scale 0.96 + brightness +4%, 100 ms.
- Pull-to-refresh: brass sprout SVG unfurls leaves as you pull.

Per-screen (overlay callouts on each frame):
- Splash: leaf SVG path unfurls, wordmark letters cascade 40 ms apart.
- Home: sections stagger in from bottom, 80 ms apart.
- Task checkbox: brass check stroke-dashoffset, then 4 leaves burst up + fade.
- Calendar: grid staggers 40 ms, current-month border pulses once.
- Crop card → detail: shared-element hero image (300 ms), title rises,
  accordion sections fade-in 60 ms stagger.
- Timeline bar: 12 columns fill L→R over 600 ms on first visible.
- Pest detail risk chip: glow pulse on entry.
- Search field focus: expands 4 dp, keyboard slides, recent chips slide up.
- Add-planting review: final card does 3D y-flip, 400 ms.
- Planting progress bar: SVG wave liquid fill, 2 s drift loop.
- Task mark-done: strike-through animates, row slides out left, reflow 250 ms.
- Dark-mode toggle: circular ink-drop reveal from switch, 500 ms.
- FAB: scale-in from 0 on screen enter, 350 ms overshoot.
- Empty-state: 2 s y-float ±3 dp loop.
- Toasts: slide down from top, 200 ms spring.
- Offline banner: sage bar slides down, persists until reconnected.

══════════════════════════════════════════════════════════════════════
9. SCREENS — generate exactly these (no extras, no omissions)
══════════════════════════════════════════════════════════════════════
Name files "NN-screen-name-light.html" and "NN-screen-name-dark.html".

A. SYSTEM
   1. Splash
   2. Onboarding 1 — "Planting, tuned for Papamoa"
   3. Onboarding 2 — "150+ crops & dwarf fruit trees"
   4. Onboarding 3 — "Stay ahead of the pests"
   5. Onboarding 4 — "Your garden, month by month"
   6. Notification permission prompt

B. MAIN TABS
   7.  Home / This Month
   8.  Calendar (12-month grid)
   9.  Month detail — Seed tab
   10. Month detail — Seedling tab
   11. Month detail — Harvest tab
   12. Crops list (grid + filter chips)
   13. Crops list — filter active "Fruit trees"
   14. Garden — empty state
   15. Garden — populated (3+ plantings, mixed stages)
   16. More — bottom sheet (Pests, Diseases, Search, Favourites, Settings)

C. DETAILS
   17. Crop detail — vegetable (Tomato 'Money Maker')
   18. Crop detail — fruit tree (Meyer Lemon: yrs-to-fruit, pot size,
       pruning schedule, winter chill)
   19. Crop detail — herb (Basil 'Genovese', compact)
   20. Planting detail (in-progress tomato, day 34/90)
   21. Pest list
   22. Pest detail — Greenhouse whitefly (3-tab controls: Organic /
       Biological (NZ) / Chemical)
   23. Disease list
   24. Disease detail — Botrytis
   25. Favourites screen

D. FLOWS
   26. Search — empty
   27. Search — typing
   28. Search — results grouped (Crops/Pests/Diseases/Videos)
   29. Add planting — step 1 pick crop
   30. Add planting — step 2 sow date
   31. Add planting — step 3 location (greenhouse/outdoor + bay)
   32. Add planting — step 4 notes
   33. Add planting — review (3D flip callout)
   34. Tasks — Today
   35. Tasks — All (grouped Today / Tomorrow / This week / Later)
   36. Task detail (check off, snooze, reschedule)
   37. Companions view (compatibility grid for chosen crop)
   38. Videos list (curated for current month)

E. SETTINGS + SYSTEM
   39. Settings
   40. Notification time picker modal
   41. Dark-mode picker modal
   42. Reset-data confirmation dialog
   43. About / credits
   44. Data version info
   45. Error state (generic)
   46. Offline banner overlaid on Home
   47. Notification preview (Android shade render)

F. STYLE GUIDE
   48. Color tokens
   49. Type scale
   50. Button + chip states
   51. Card anatomy
   52. Timeline bar component
   53. Progress bar + stage chips
   54. Bottom nav spec (active + inactive + hairline)
   55. App bar spec (light + dark side-by-side)
   56. Icon set sheet (all icons at 24 and 48 dp)
   57. Motion spec (curves, durations, examples)

══════════════════════════════════════════════════════════════════════
10. UNIVERSAL RULES
══════════════════════════════════════════════════════════════════════
- App bar fill: Olive (light) / Cream (dark). Always.
- Bottom nav: sticky on main tabs, hidden on details.
- Detail screens: sticky bottom action bar with primary + ghost.
- Every eyebrow: leaf glyph + brass small-caps.
- Every image card: 0.5 dp brass top-corner hairline + warm shadow.
- Display titles in body: Olive Moss.
- Data numerals on detail screens: brass, Inter Tabular.
- Fruit-tree crop detail extra sections: Pot + rootstock, Pollination,
  Winter chill, Pruning schedule.
- Pest detail controls: 3 tabs — Organic / Biological (NZ) / Chemical.
- Search groups: Crops / Pests / Diseases / Videos.
- Garden progress bar colour by stage: seed=honey, seedling=terracotta,
  growing=forest, harvest=sprout.

══════════════════════════════════════════════════════════════════════
11. REALISTIC COPY — no lorem ipsum
══════════════════════════════════════════════════════════════════════
Greeting: "Good morning" / "Good afternoon" / "Good evening".
Subtitle: current month name in Fraunces italic.
Tasks: "Transplant 4 tomato seedlings" · "Check whitefly traps in Bay 1" ·
       "Water potted citrus" · "Pinch basil tips" · "Prune cherry tomatoes".
Crops: Tomato 'Money Maker', Cucumber 'Lebanese', Capsicum 'Californian
       Wonder', Zucchini 'Black Beauty', Basil 'Genovese', Lettuce
       'Buttercrunch', Eggplant 'Long Purple', Kale 'Cavolo Nero', Meyer
       Lemon, Fig 'Brown Turkey', Tamarillo, Strawberry 'Camarosa',
       Kūmara 'Owairaka Red'.
Pests: Greenhouse whitefly, Tomato-potato psyllid, Aphid, Two-spotted spider
       mite, Western flower thrips, Fungus gnats, Mealybug, Citrus leafminer.
Latin names in italic under common names; Māori names where relevant.

══════════════════════════════════════════════════════════════════════
12. DELIVERABLES
══════════════════════════════════════════════════════════════════════
- 57 screens × 2 (light + dark) + 10 style-guide frames.
- HTML + CSS per frame.
- ONE design-tokens.json covering colors, type scale, spacing, radius,
  shadow, motion curves and durations.
- DO NOT generate: Video Player, Greenhouse Monitor, placeholders, or
  duplicates.
- DO NOT skip any screen in §9.

Generate all frames in one coherent pass.
````

---

## 10. Image + video asset pipeline

- **Photos:** your own or CC0/CC-BY from Wikimedia / Pixabay / Unsplash. Record licence + attribution per file in `art-source/CREDITS.csv`.
- **Export:** 1200×800 hero, 400×300 thumb, WebP q=80.
- **Pre-commit hook:** a Python script converts any PNG/JPG dropped into `art-source/` to correctly-sized WebPs in `res/drawable-nodpi/`.
- **Videos:** curated YouTube IDs only. Keep a per-item allow-list in `seed/videos.json`. Annual review.

---

## 11. Seed data — crops

Times are for **Papamoa / warm NZ zone, under greenhouse cover**. Outdoor shifts 2–4 weeks later for spring starts. S→S = seed to transplant-ready. S→H = seedling to first harvest.

### 11.1 Vegetables, herbs, leafy, roots, alliums, legumes, microgreens

| # | Crop | Cat | Sow seed | Plant seedling | Harvest | S→S | S→H | Notes |
|---|---|---|---|---|---|---|---|---|
| 1 | Tomato (indeterm.) | FR | Jul–Oct | Sep–Dec | Dec–May | 6–8 w | 10–14 w | Stake + single-leader prune |
| 2 | Tomato (cherry) | FR | Jul–Nov | Sep–Dec | Dec–May | 6–8 w | 8–10 w | |
| 3 | Tomato (beefsteak) | FR | Jul–Sep | Sep–Nov | Jan–Apr | 7–9 w | 12–16 w | Heaviest feeder |
| 4 | Tomato (roma/paste) | FR | Jul–Oct | Sep–Dec | Jan–May | 6–8 w | 10–13 w | Sauce varieties |
| 5 | Capsicum | FR | Jul–Sep | Oct–Nov | Jan–May | 7–9 w | 11–14 w | Needs heat mat |
| 6 | Chilli | FR | Jul–Sep | Oct–Nov | Jan–Jun | 8–10 w | 12–16 w | Slow, start early |
| 7 | Eggplant / aubergine | FR | Jul–Sep | Oct–Nov | Jan–May | 7–9 w | 12–14 w | Heat lover |
| 8 | Tomatillo | FR | Aug–Oct | Oct–Dec | Jan–Apr | 6–8 w | 10–12 w | Needs 2+ plants |
| 9 | Cape gooseberry | FR | Aug–Oct | Oct–Dec | Feb–May | 7–9 w | 14–18 w | Perennial in GH |
| 10 | Pepino | FR | — | Sep–Dec (cutting) | Jan–May | — | 16–20 w | Hedge/trellis |
| 11 | Okra | FR | Sep–Nov | Oct–Dec | Jan–Apr | 3–4 w | 8–10 w | GH only in BOP |
| 12 | Cucumber | CU | Aug–Jan | Sep–Feb | Dec–Apr | 3–4 w | 7–10 w | Trellis |
| 13 | Gherkin | CU | Sep–Jan | Oct–Feb | Dec–Apr | 3 w | 6–8 w | Pick small |
| 14 | Zucchini / courgette | CU | Sep–Jan | Oct–Feb | Dec–Apr | 3–4 w | 6–8 w | |
| 15 | Rockmelon | CU | Sep–Nov | Oct–Dec | Feb–Apr | 3–4 w | 10–12 w | GH preferred |
| 16 | Watermelon (small) | CU | Sep–Nov | Oct–Dec | Feb–Apr | 3–4 w | 12–14 w | GH preferred |
| 17 | Honeydew melon | CU | Sep–Nov | Oct–Dec | Feb–Apr | 3–4 w | 12–14 w | |
| 18 | Pumpkin (Crown) | CU | Sep–Dec | Oct–Jan | Mar–May | 3–4 w | 14–16 w | Outdoor OK |
| 19 | Buttercup squash | CU | Sep–Dec | Oct–Jan | Mar–May | 3–4 w | 12–14 w | |
| 20 | Butternut | CU | Sep–Dec | Oct–Jan | Mar–May | 3–4 w | 14–16 w | |
| 21 | Spaghetti squash | CU | Sep–Dec | Oct–Jan | Feb–Apr | 3–4 w | 12–14 w | |
| 22 | Marrow | CU | Sep–Dec | Oct–Jan | Dec–Apr | 3–4 w | 8–10 w | |
| 23 | Luffa | CU | Sep–Nov | Oct–Dec | Apr–Jun | 3–4 w | 26–30 w | GH only |
| 24 | Basil (sweet) | HB | Sep–Feb | Oct–Mar | Dec–May | 3–4 w | 6–8 w | Pinch tips |
| 25 | Thai basil | HB | Sep–Feb | Oct–Mar | Dec–May | 3–4 w | 6–8 w | |
| 26 | Coriander | HB | Feb–May, Aug–Oct | Mar–Jun, Sep–Nov | year | 3–4 w | 5–7 w | Bolts in heat |
| 27 | Parsley (flat + curly) | HB | Jul–Apr | Aug–May | year | 4–6 w | 8–10 w | |
| 28 | Mint | HB | Sep–Mar | Sep–Apr | year | 4–6 w | 8 w | Contain in pot |
| 29 | Chives | HB | Aug–Mar | Sep–Apr | year | 6–8 w | 10–12 w | Clump-divide |
| 30 | Garlic chives | HB | Aug–Mar | Sep–Apr | year | 6–8 w | 10–12 w | |
| 31 | Dill | HB | Sep–Mar | — direct sow | Nov–May | — | 8–10 w | Don't transplant |
| 32 | Oregano | HB | Sep–Feb | Oct–Mar | year | 4–6 w | 10–12 w | Perennial |
| 33 | Thyme | HB | Sep–Feb | Oct–Mar | year | 6–8 w | 12–14 w | Perennial |
| 34 | Rosemary | HB | — | year (cutting) | year | — | 12 w | Perennial, pot OK |
| 35 | Sage | HB | Sep–Feb | Oct–Mar | year | 4–6 w | 10–14 w | Perennial |
| 36 | French tarragon | HB | — | Sep–Nov (root div.) | Nov–May | — | 10–12 w | Must be French |
| 37 | Lemon balm | HB | Sep–Feb | Oct–Mar | year | 4–6 w | 10 w | Pot it, spreads |
| 38 | Lemongrass | HB | — | Oct–Dec (division) | Feb–May | — | 16–20 w | GH ideal |
| 39 | Chervil | HB | Feb–Apr, Aug–Oct | Mar–May, Sep–Nov | May–Nov | 3–4 w | 6–8 w | |
| 40 | Stevia | HB | Oct–Dec | Nov–Jan | Feb–May | 6–8 w | 12–14 w | GH frost-free |
| 41 | Shiso / perilla | HB | Sep–Dec | Oct–Jan | Dec–Apr | 4–6 w | 8–10 w | |
| 42 | Lettuce (looseleaf) | LF | year | year | year | 3–4 w | 6–8 w | Shade Jan–Feb |
| 43 | Cos / romaine | LF | Feb–Oct | Mar–Nov | Apr–Dec | 3–4 w | 8–10 w | |
| 44 | Mesclun mix | LF | year | year | year | 2–3 w | 4 w | Cut-and-come |
| 45 | Spinach | LF | Feb–Sep | Mar–Oct | May–Nov | 3–4 w | 5–7 w | Bolts in heat |
| 46 | NZ spinach | LF | Sep–Mar | Oct–Apr | Nov–May | 3–4 w | 8–10 w | Heat-tolerant |
| 47 | Silverbeet | LF | year | year | year | 4–6 w | 8–10 w | |
| 48 | Rocket | LF | Feb–Oct | Mar–Nov | Apr–Nov | 3 w | 4–6 w | |
| 49 | Endive | LF | Feb–Apr, Aug–Oct | Mar–May, Sep–Nov | May–Dec | 4–6 w | 10–12 w | Blanch for mild |
| 50 | Radicchio | LF | Feb–Apr, Aug–Oct | Mar–May, Sep–Nov | May–Dec | 4–6 w | 12–14 w | |
| 51 | Watercress | LF | Sep–Apr | Oct–May | year | 3–4 w | 6–8 w | Constant moisture |
| 52 | Sorrel | LF | Aug–Mar | Sep–Apr | year | 4–6 w | 10–12 w | Perennial |
| 53 | Corn salad / mâche | LF | Feb–May, Aug–Oct | Mar–Jun, Sep–Nov | May–Nov | 3–4 w | 8–10 w | Winter green |
| 54 | Amaranth (callaloo) | LF | Sep–Feb | Oct–Mar | Dec–May | 3–4 w | 6–8 w | Heat lover |
| 55 | Microgreens (mix) | MG | year | — direct | year | — | 7–21 d | Tray growing |
| 56 | Kale (curly + cavolo) | BR | Jan–Apr, Jul–Sep | Feb–May, Aug–Oct | May–Nov | 4–6 w | 8–10 w | |
| 57 | Broccoli | BR | Jan–Apr, Jul–Sep | Feb–May, Aug–Oct | Apr–Nov | 4–6 w | 10–12 w | |
| 58 | Broccolini | BR | Jan–Apr, Jul–Sep | Feb–May, Aug–Oct | Apr–Nov | 4–6 w | 10–12 w | |
| 59 | Cauliflower | BR | Jan–Mar, Jul–Aug | Feb–Apr, Aug–Sep | May–Oct | 4–6 w | 12–14 w | |
| 60 | Cabbage (round + savoy) | BR | year | year | year | 4–6 w | 10–14 w | |
| 61 | Chinese cabbage / wombok | BR | Jan–Apr, Aug–Oct | Feb–May, Sep–Nov | Apr–Dec | 3–4 w | 8–10 w | Bolts in heat |
| 62 | Bok choy | BR | year | year | year | 3–4 w | 6–8 w | |
| 63 | Choi sum | BR | year | year | year | 3–4 w | 5–7 w | |
| 64 | Tatsoi | BR | Feb–Oct | Mar–Nov | Apr–Nov | 3–4 w | 5–7 w | |
| 65 | Mizuna | BR | Feb–Oct | Mar–Nov | Apr–Nov | 3 w | 4–6 w | |
| 66 | Mustard greens | BR | Feb–Oct | Mar–Nov | Apr–Nov | 3 w | 5–7 w | |
| 67 | Kohlrabi | BR | Jan–Apr, Aug–Oct | Feb–May, Sep–Nov | Apr–Dec | 4–6 w | 8–10 w | |
| 68 | Brussels sprouts | BR | Oct–Jan | Nov–Feb | May–Aug | 4–6 w | 24–28 w | Long grower |
| 69 | Turnip | BR | Feb–Oct | — direct sow | Apr–Dec | — | 6–10 w | |
| 70 | Swede | BR | Nov–Feb | — direct sow | Apr–Jul | — | 18–24 w | |
| 71 | Daikon | BR | Feb–May, Aug–Oct | — direct sow | May–Nov | — | 8–10 w | |
| 72 | Radish | BR | year | — direct sow | year | — | 4–6 w | Fastest crop |
| 73 | Carrot | RT | year | — direct sow | year | — | 10–14 w | Don't transplant |
| 74 | Beetroot | RT | year | year | year | 3–4 w | 8–10 w | |
| 75 | Parsnip | RT | Aug–Mar | — direct sow | Jan–Aug | — | 16–20 w | |
| 76 | Celeriac | RT | Aug–Oct | Sep–Nov | Feb–May | 8–10 w | 20–24 w | |
| 77 | Potato | RT | — | Jul–Dec (tuber) | Nov–May | — | 14–20 w | Chit first |
| 78 | Kūmara | RT | — | Oct–Dec (slip) | Apr–May | — | 20–24 w | Needs warm soil |
| 79 | Oca (NZ yam) | RT | — | Sep–Nov (tuber) | Jun–Aug | — | 28–32 w | Harvest after frost |
| 80 | Jerusalem artichoke | PER | — | Aug–Oct (tuber) | May–Aug | — | 36–40 w | Perennial, spreads |
| 81 | Horseradish | PER | — | Aug–Oct (crown) | Jun–Sep | — | 40+ w | Contain |
| 82 | Onion (long-day NZ) | AL | Mar–Jul | Apr–Aug | Dec–Feb | 6–8 w | 24+ w | |
| 83 | Spring onion | AL | year | year | year | 4–6 w | 8–10 w | |
| 84 | Leek | AL | Aug–Dec | Oct–Feb | Mar–Aug | 8–10 w | 20–24 w | |
| 85 | Shallot | AL | — | Jun–Aug (bulb) | Dec–Jan | — | 20–24 w | |
| 86 | Garlic | AL | — | Jun (shortest day) | Nov–Jan | — | 24–28 w | Plant cloves |
| 87 | Elephant garlic | AL | — | May–Jul (clove) | Dec–Jan | — | 26–30 w | |
| 88 | Pea (dwarf) | LG | Mar–Sep | Apr–Oct | Jun–Dec | 3–4 w | 10–12 w | |
| 89 | Snow pea | LG | Mar–Sep | Apr–Oct | Jun–Dec | 3–4 w | 10–12 w | |
| 90 | Sugar snap pea | LG | Mar–Sep | Apr–Oct | Jun–Dec | 3–4 w | 10–12 w | |
| 91 | Broad bean | LG | Mar–Jul | — direct sow | Sep–Dec | — | 20–24 w | |
| 92 | Green bean (bush) | LG | Sep–Feb | Oct–Feb | Dec–May | 2–3 w | 8–10 w | |
| 93 | Climbing bean | LG | Sep–Jan | Oct–Feb | Dec–May | 2–3 w | 10–12 w | Trellis |
| 94 | Runner bean | LG | Sep–Dec | Oct–Jan | Jan–May | 2–3 w | 10–12 w | Perennial in BOP |
| 95 | Edamame (soybean) | LG | Oct–Dec | Nov–Jan | Feb–Apr | 2–3 w | 12–14 w | |
| 96 | Sweetcorn | OT | Sep–Dec | Oct–Jan | Jan–Apr | 3 w | 12–14 w | Block-plant |
| 97 | Celery | OT | Aug–Oct | Sep–Nov | Feb–May | 8–10 w | 14–18 w | Needs moisture |
| 98 | Florence fennel | OT | Feb–Apr, Aug–Sep | Mar–May, Sep–Oct | May–Dec | 4–6 w | 10–14 w | |
| 99 | Rhubarb | PER | — | Jun–Aug (crown) | Sep–Jan | — | 1 yr | Perennial |
| 100 | Asparagus | PER | — | Jun–Aug (crown) | Sep–Dec | — | 2–3 yr | Perennial |
| 101 | Globe artichoke | PER | Aug–Oct | Sep–Nov (or div.) | Oct–Jan | 8–10 w | 1 yr | Perennial |
| 102 | Ginger | PER | — | Sep–Nov (rhizome) | Jun–Aug | — | 32–40 w | GH, Papamoa-warm |
| 103 | Turmeric | PER | — | Sep–Nov (rhizome) | Jun–Aug | — | 36–40 w | GH only |
| 104 | Strawberry | VB | — | May–Aug (runner) | Oct–Jan | — | ongoing | Everbearers |

### 11.2 Small fruit trees + berry vines (dwarf / container candidates for greenhouse)

All container-grown unless noted. Pot sizes are **minimum at planting** — most upsize to 40–60 L as they mature. "Yrs→fruit" is typical for grafted dwarf stock.

| # | Tree/vine | Cat | Plant month | Pot | Yrs→fruit | Harvest | Notes |
|---|---|---|---|---|---|---|---|
| 105 | Meyer lemon (dwarf) | FT | year (spring best) | 40–50 L | 1–2 | year-round | Classic pot citrus |
| 106 | Tahitian lime (dwarf) | FT | Sep–Nov | 40–50 L | 1–2 | Apr–Aug | Needs shelter |
| 107 | Kaffir lime | FT | Sep–Nov | 30–40 L | 2–3 | leaves year | Leaves > fruit |
| 108 | Kumquat Meiwa | FT | year | 30–40 L | 1–2 | Jun–Sep | Hardiest citrus |
| 109 | Calamondin orange | FT | year | 30–40 L | 1–2 | year | Sour, ornamental |
| 110 | Satsuma mandarin Miho | FT | Sep–Nov | 40–50 L | 2–3 | May–Aug | Easy peel |
| 111 | Clementine mandarin | FT | Sep–Nov | 40–50 L | 2–3 | Jun–Aug | |
| 112 | Washington navel (dwarf) | FT | Sep–Nov | 50 L | 2–3 | Jul–Oct | |
| 113 | Grapefruit (dwarf) | FT | Sep–Nov | 50 L | 2–3 | Jul–Oct | |
| 114 | Yuzu | FT | Sep–Nov | 40 L | 3–4 | May–Jul | Cold-tolerant |
| 115 | Fig 'Brown Turkey' | FT | Jun–Aug (bare-root) | 40–50 L | 2 | Jan–Apr | Two crops possible |
| 116 | Fig 'Petite Negra' | FT | Jun–Aug | 20–30 L | 1–2 | Jan–Apr | Tiny tree, fruits |
| 117 | Peach 'Bonanza' (dwarf) | FT | Jun–Aug | 40–50 L | 2 | Dec–Feb | Self-fertile |
| 118 | Peach 'Honey Babe' (dwarf) | FT | Jun–Aug | 40–50 L | 2 | Jan–Feb | Self-fertile |
| 119 | Peach 'Pixzee' (dwarf) | FT | Jun–Aug | 30–40 L | 2 | Dec–Jan | 1.8 m max |
| 120 | Nectarine 'Flavourzee' | FT | Jun–Aug | 40 L | 2 | Jan–Feb | Self-fertile |
| 121 | Nectarine 'Nectar Babe' | FT | Jun–Aug | 40 L | 2 | Jan–Feb | Low chill |
| 122 | Apricot 'Garden Annie' | FT | Jun–Aug | 40 L | 2–3 | Dec–Jan | Low chill |
| 123 | Cherry 'Griotella' (dwarf) | FT | Jun–Aug | 40–50 L | 3 | Dec | Morello, sour |
| 124 | Plum (dwarf Santa Rosa) | FT | Jun–Aug | 40–50 L | 2–3 | Jan–Feb | |
| 125 | Almond 'Garden Prince' | FT | Jun–Aug | 40–50 L | 3 | Feb–Mar | 1.5 m dwarf |
| 126 | Apple 'Blush Babe' (dwarf) | FT | Jun–Aug | 40–50 L | 2 | Feb–Apr | 2 m max |
| 127 | Apple 'Thumbelina' Candy Crunch | FT | Jun–Aug | 30–40 L | 2 | May | Kid-friendly |
| 128 | Mulberry 'Black English' | FT | Jun–Aug | 40 L | 2–3 | Dec–Jan | |
| 129 | Pomegranate dwarf 'Nana' | FT | Sep–Nov | 40 L | 2–3 | Apr–Jun | Ornamental too |
| 130 | Feijoa 'Bambina' (dwarf) | FT | Apr–Oct | 40 L | 2–3 | Apr–Jun | NZ classic |
| 131 | Guava 'Hawaiian' (dwarf) | FT | Sep–Nov | 30–40 L | 2–3 | Mar–May | GH frost-free |
| 132 | Olive (potted) | FT | Sep–Nov | 40–60 L | 3–4 | May–Jul | Needs ripening heat |
| 133 | Avocado 'Wurtz' / Little Cado | FT | Sep–Nov | 50–80 L | 3–5 | Apr–Oct | GH early years |
| 134 | Macadamia (container young) | FT | Sep–Nov | 50–80 L | 4–6 | Mar–Jun | Slow |
| 135 | Tamarillo (tree tomato) | FT | Sep–Nov | 40 L | 2 | Apr–Sep | Stellar for BOP GH |
| 136 | Dwarf banana 'Cavendish' | FT | Sep–Dec | 50–80 L | 1–2 | year after flower | GH, 2–2.5 m |
| 137 | Coffee arabica | FT | Sep–Dec | 30–40 L | 3–4 | Aug–Oct | Novelty GH plant |
| 138 | Blueberry dwarf 'Top Hat' | VB | Jun–Aug | 30 L | 2 | Dec–Feb | Acid pH 4.5–5.5 |
| 139 | Raspberry 'Raspberry Shortcake' | VB | Jun–Aug | 30–40 L | 1–2 | Dec–Feb | Thornless dwarf |
| 140 | Blackberry (thornless) | VB | Jun–Aug | 40 L | 1–2 | Jan–Mar | Trellis |
| 141 | Boysenberry | VB | Jun–Aug | 40 L | 2 | Dec–Jan | Trellis |
| 142 | Loganberry | VB | Jun–Aug | 40 L | 2 | Dec–Jan | |
| 143 | Gooseberry | VB | Jun–Aug | 30–40 L | 2 | Dec–Jan | Cool climates |
| 144 | Redcurrant | VB | Jun–Aug | 30 L | 2 | Dec–Jan | |
| 145 | Blackcurrant | VB | Jun–Aug | 30 L | 2 | Dec–Jan | |
| 146 | Cranberry | VB | Jun–Aug | 30 L (wide) | 2–3 | Apr–Jun | Acid, wet |
| 147 | Goji berry | VB | Sep–Nov | 30 L | 2–3 | Feb–May | |
| 148 | Passionfruit 'Black' | VB | Sep–Nov | 50 L | 1–2 | Feb–Jun | Trellis, GH-ideal |
| 149 | Passionfruit 'Panama Gold' | VB | Sep–Nov | 50 L | 1–2 | Feb–Jun | Trellis |
| 150 | Kiwifruit Hayward + Tomuri | VB | Jun–Aug | 50 L each | 3–4 | Apr–Jun | Need both sexes |
| 151 | Grape (table, 'Niagara') | VB | Jun–Aug | 50 L | 2–3 | Feb–Apr | Trellis |
| 152 | Dragon fruit (pitaya) | VB | Sep–Nov | 40 L | 2–3 | Mar–May | Cactus, trellis |
| 153 | Pineapple (pot, top-rooted) | VB | Sep–Nov | 20–30 L | 2–3 | Dec–Mar | GH, warm |

**Category key:** FR fruiting veg · CU cucurbit · HB herb · LF leafy · BR brassica · RT root · AL allium · LG legume · MG microgreen · PER perennial · FT fruit tree · VB vine/berry · OT other

**Universal sowing rule:** depth = 2–3× seed diameter · seed-raising mix damp not wet · bottom heat 20–25 °C for Solanaceae · prick out at first true leaves · harden off 7 days before transplant.

**Universal fruit-tree rule:** free-draining citrus/fruit mix · annual root prune every 2–3 yr · slow-release citrus food in spring + compost tea monthly Oct–Mar · mulch surface · winter-chilly stone fruit (peach/nectarine/cherry/apricot/plum/apple) must be **moved outdoors Jun–Aug** to meet chill requirement, then returned to GH for flowering/fruiting.

### 11.3 Approved YouTube channels for curated video IDs

Curate `youtubeVideoIds` from these — NZ/Australia voices first, international as fallback:

- **NZ:** Tui Garden · Kings Plant Barn · Daltons Garden Tips · Palmers Gardenworld · Khang Starr · NZ Gardener magazine · Edible Backyard (Kath Irvine)
- **AU (similar climate):** Self Sufficient Me · Gardening Australia · The Gardening Channel with James Prigioni
- **UK / US (method videos):** Huw Richards · Charles Dowding · Epic Gardening · MIgardener · GrowVeg

Quality bar: ≤15 min, good audio, no clickbait, no pesticide promo. Re-verify every 12 months.

---

## 12. Seed data — pests (greenhouse priority for BOP)

Keep each entry ≤80 words in-app.

1. **Greenhouse whitefly** (*Trialeurodes vaporariorum*) — NZ's biggest GH pest. White fly on leaf undersides, sticky honeydew, sooty mould. **Organic:** remove leaves, hose, soap. **Biological:** *Encarsia formosa* (>18 °C), *Eretmocerus eremicus*. **Prevention:** yellow sticky traps, fine mesh on vents, end-of-season hot cleanout.
2. **Tomato-potato psyllid** — serious NZ Solanaceae pest. Sugar granules on leaves, psyllid yellows. **Prevention:** mesh <0.35 mm, remove volunteers, yellow traps. **Biological:** *Tamarixia triozae*. **Last resort:** spinosad, neem.
3. **Aphid** (green peach, etc.) — cluster on new growth, spread virus. **Organic:** squash, hose, soap. **Biological:** *Aphidius colemani / ervi*, *Aphidoletes*. Encourage ladybirds, hoverflies.
4. **Two-spotted spider mite** — tiny, webbing, yellow stippling; thrives hot+dry. **Organic:** raise humidity, mist. **Biological:** *Phytoseiulus persimilis*. Neem, sulphur (not with oils).
5. **Western flower thrips / onion thrips** — silvering, black specks, TSWV vector. **Organic:** blue sticky traps, mesh. **Biological:** *Neoseiulus cucumeris*, *Orius*. Spinosad last resort.
6. **Fungus gnats** — black flies around seedlings; larvae chew roots. **Organic:** surface dry between waters, yellow traps. **Biological:** *Steinernema feltiae* nematode drench, BTi.
7. **Caterpillars** (tomato fruitworm, looper, cabbage white) — chewed leaves, bored fruit. **Organic:** hand-pick, BT (Dipel), insect netting.
8. **Mealybug** — white cottony clumps, sticky. **Organic:** meths on cotton bud, oil. **Biological:** *Cryptolaemus montrouzieri*.
9. **Slugs and snails** — Papamoa humidity brings them to GH perimeters. **Organic:** iron-phosphate (Tui Quash), beer traps, copper tape on benches.
10. **Scale insects** — brown limpets on stems. **Organic:** scrub + soapy water, horticultural oil, ladybirds.
11. **Citrus leafminer** — silver trails in new citrus leaves. **Organic:** prune affected flushes, neem or horticultural oil on new flush. **Prevention:** pheromone traps.
12. **Passion vine hopper** — bubbly spit on passionfruit stems. **Organic:** spray with neem early morning; encourage predators.

**General greenhouse hygiene (link from every pest):** insect mesh on all vents/doors, end-of-season cleanout (remove residue, 1-week hot bake >35 °C), quarantine new plants 7–10 days, weed-free 1 m strip outside, monitoring sticky traps year-round.

---

## 13. Seed data — diseases

1. **Botrytis / grey mould** — RH>80 %, 15–22 °C. Vent more, no overhead water, remove old leaves.
2. **Powdery mildew** — cucurbits + tomato. Airflow, potassium bicarbonate, milk 1:10, resistant varieties.
3. **Tomato late blight** — Aug–Oct wet spells. Airflow, dry foliage, preventive copper.
4. **Damping off** — sterile mix, don't overwater, chamomile drench.
5. **TSWV (tomato-spotted wilt)** — thrips-vectored. Control thrips, bin infected plants.
6. **Citrus gall wasp** — swollen green stems on citrus. Prune out and bag galls Jun–Aug before wasp emerges.
7. **Blossom end rot** (disorder, not disease) — erratic watering / calcium. Consistent watering, mulch, dolomite pre-plant.
8. **Clubroot** (brassicas) — swollen roots. Rotate 4+ yr, lime soil to pH 7+, don't compost affected plants.

---

## 14. Search specification

- Single text input, no advanced filters v1.
- Matches: crop common name, aliases (incl. Māori, UK/US), family, pest, disease.
- FTS4 with prefix queries (`basil*` matches "basil").
- Scoring: exact common-name > alias > pest/disease.
- Results grouped: **Crops · Pests · Diseases · Videos** (video group shows any curated video whose stored title matches).
- Row template: thumbnail, title, one-line subtitle (e.g. "Solanaceae · sow Jul–Oct").
- Recent searches: 10 max, tap to re-run, long-press to delete.
- Empty + no-results states have a friendly botanical illustration and suggestions.

---

## 15. Definition of done — v1

- Installs and runs offline on Android 8+.
- Home shows correct current-month lists.
- 150+ crops/fruit trees and 10+ pests seeded, all with images.
- Calendar, crop detail, pest detail, search, garden, settings reachable.
- Adding a planting generates tasks and fires a notification on the due date.
- Tap any video card opens YouTube via intent (or falls back to browser).
- Play pre-launch: 0 crashes, 0 ANRs.
- APK <40 MB (thumbnails are remote, so size stays down).
- TalkBack a11y pass.
- Design matches the superdesign.dev mockups within reasonable Compose-parity tolerance (deltas logged).

---

## 16. Post-v1 ideas (do not build yet)

- NIWA/MetService weather tie-in (frost + heatwave alerts).
- Photo diary per planting.
- Te reo Māori language toggle (strings are ready).
- BLE temp/RH sensor integration.
- Shared seed-swap board (needs backend).
- Inline YouTube player via Android YouTube Player API.

---

## 17. References

- NIWA climate data for Tauranga/BOP
- Tui Garden, Daltons, Kings Plant Barn planting calendars
- Bioforce NZ biological control guides
- Waimea Nurseries dwarf fruit-tree catalogue
- Bayer Vegetables protected-culture tomato guide
- KoruKai Herb Farm sowing notes
- Gardening Know How / Growing Spaces greenhouse fruit-tree guides

---

**End of plan v2. Start with Phase 0.**
