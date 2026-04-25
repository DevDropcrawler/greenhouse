# design-coverage.md

Maps every screen the agent will build to its design source. Read this before building any UI.

**Design baseline location:** `assets/superdesignhtml/`
**Design tokens (canonical):** plan §9 (color, type, shape, motion)
**Theme implementation:** `app/src/main/java/.../ui/theme/` (Phase 0 output)

## Three sources of truth

1. **Have HTML** → use the file in `assets/superdesignhtml/` as visual reference. Match layout, spacing, colour, type from it.
2. **Build from tokens** → no HTML exists. Use `Theme.kt` tokens + the visual conventions established by the existing 27 frames (olive app bar, brass hairlines, 22 dp radius, Fraunces display + Inter body, leaf eyebrows). Match the look — do not improvise a new style.
3. **Skip** → not in v1.

## Two universal design overrides (apply to every screen)

The 27 HTML files were generated across multiple iterations and are not internally consistent on these two points. The plan wins:

1. **Bottom nav tabs (plan §9):** canonical 5 tabs are **Home, Calendar, Crops, Garden, More**. Some HTML files show Home/Crops/Calendar/Pests/Settings — ignore that variant. Use the planned 5 tabs everywhere.
2. **Light theme only.** Some HTML may have dark variants — ignore them.

## Coverage table

| Plan screen | Source | File / fallback |
|---|---|---|
| Splash | HTML | `01-standardized-header-implementation-splash-screen.html` |
| Onboarding | HTML | `02-standardized-header-for-onboarding-screen.html` |
| Notification permission prompt | Tokens | match onboarding style, single-card layout |
| Home / This Month | HTML | `04-standardized-header-system-home-screen.html` |
| Calendar (12-month grid) | HTML | `03-standardized-navigation-calendar-screen.html` |
| Month detail — Seed tab | HTML | `24-month-detail-standardized-olive-header.html` (canonical for all 3 tabs; just swap data) |
| Month detail — Seedling tab | Reuse | same layout as Seed tab |
| Month detail — Harvest tab | Reuse | same layout as Seed tab |
| Crops list | HTML | `05-garden-screen-standardized-crops-header.html` (note misleading filename — content is the Crops list) |
| Crops list — filter active | Reuse | 05 with chip selected state from frame |
| Garden — empty | Tokens | no canonical empty-state HTML — build a botanical empty state matching the populated screen's visual language |
| Garden — populated | HTML | `27-standardized-olive-header-garden-screen.html` (canonical — has the multi-card layout with stage-tinted progress bars, "Need Attention" flag, FAB) |
| More sheet | HTML | `07-standardized-header-system-more-screen.html` |
| Crop detail — vegetable (Tomato) | HTML | `08-standardized-header-for-tomato-detail.html` |
| Crop detail — fruit tree (Meyer Lemon) | HTML | `15-standardized-header-for-meyer-lemon-detail.html` |
| Crop detail — herb (Basil) | HTML | `19-standardized-header-spec-update-basil-detail.html` |
| Planting detail | HTML | `25-standardized-header-color-sync-planting-detail.html` (canonical — use this, not 20) |
| Pest list | HTML | `09-keeley-s-greenhouse-pest-list-standardized-header-system.html` |
| Pest detail (Whitefly) | HTML | `11-standardized-header-for-whitefly-detail.html` |
| Disease list | HTML | `16-standardized-header-system-diseases-screen.html` |
| Disease detail (Botrytis) | HTML | `17-standardized-header-implementation.html` |
| Favourites | Tokens | reuse Crops list pattern, swap title |
| Search empty | Tokens | search field at top, "Recent" chip row, botanical empty state |
| Search typing | Tokens | search field with cursor, suggestion list below |
| Search results grouped | HTML | `14-search-screen-standardized-header-style.html` |
| Add planting step 1 (pick crop) | HTML | `10-standardized-modal-header-pick-a-crop.html` |
| Add planting step 2 (sow date) | HTML | `21-add-planting-step-2-standardized-header.html` |
| Add planting step 3 (location) | HTML | `22-standardized-header-layout-add-planting-step-3.html` |
| Add planting step 4 (notes) | HTML | `23-standardized-header-and-app-bar-add-planting-step-4.html` |
| Add planting review (step 5) | HTML | `26-standardized-olive-header-update.html` |
| Tasks Today | HTML | `12-standardized-header-for-today-s-tasks.html` |
| Tasks grouped (Today/Tomorrow/This week) | Tokens | reuse 12 layout, add section eyebrows |
| Task detail | Tokens | bottom-sheet pattern, see add-planting bottom-sheet style |
| Companions view | Tokens | reuse Crops list with compatibility chips on each row |
| Videos list | Tokens | reuse Crops list card pattern, swap thumbnail to YouTube card style from crop detail "Watch & learn" section |
| Settings | HTML | `13-standardized-header-system-settings-screen.html` |
| Notification time picker | Tokens | Material 3 TimePicker dialog with olive accents |
| Reset-data confirmation | Tokens | AlertDialog, terracotta destructive button |
| About / credits | Tokens | scrolling text screen, olive headings |
| Data version | Tokens | simple list rows in Settings |
| Error state (generic) | Tokens | botanical empty-state illustration + retry button |
| Offline banner | Tokens | sage bar slides down from top, 1-line text |

## Reference-only files (do NOT use as canonical)

These are early or alternate iterations of screens that have a better canonical version:

- `06-standardized-header-system-keeley-s-greenhouse.html` — early Garden/Crops hybrid; use 27 for Garden populated, 05 for Crops list
- `18-standardized-garden-screen-header.html` — simpler one-card Garden populated; use 27
- `20-standardized-header-implementation.html` — early Planting detail; use 25

## Skipped (not in v1)

- Video Player screen (we use YouTube intents)
- Greenhouse Monitor (sensor integration is post-v1)
- Onboarding 2/3/4 (single-screen onboarding from frame 02 is sufficient)
- Style-guide screens (codified in `Theme.kt` instead)
- Dark theme (light only for v1)

## Rules for "build from tokens" screens

When the agent builds a screen marked **Tokens** above:

1. **No new colours.** Use only what's in `Theme.kt`.
2. **No new component patterns.** Reuse: app bar, card, chip, list row, bottom action bar, eyebrow label, timeline, progress bar — all already defined by the existing HTML frames.
3. **Match spacing.** 16 dp horizontal padding, 22 dp card radius, 8 dp grid for vertical rhythm — same as existing frames.
4. **Match type roles.** Display = Fraunces / olive. Eyebrow = small-caps / brass / leaf prefix. Body = Inter 16. Numerals on detail = brass tabular.
5. **Log the screen** in `docs/design-deltas.md` so we can review later: which screen, what reference frame inspired it, any decisions taken.
