# Phase Status

## Active phase
**Phase 8 — Videos + polish**

## Phase 2 goals
- Home screen with three horizontal lists: Sow from seed, Transplant, Harvest (current month from seeded crop data)
- Today's tasks strip pulled from `tasks` table
- Shortcuts to search and calendar
- ViewModel + repository for crops/tasks; Hilt-injected
- Match superdesign mockup for Home (light theme, olive app bar, sticky bottom nav)

## Done when
- `./gradlew assembleDebug` is green
- VM happy-path tests pass
- App boots and shows current-month sow/transplant/harvest lists
- Zero new lint errors
- Screenshot at `docs/screenshots/phase-2.png`
- Commit message: `phase 2: home / this month`

## Phase history

| # | Phase | Status | Notes |
|---|---|---|---|
| 0 | Skeleton | done | 2026-04-26 — project, Hilt app, empty scaffold, version catalog, light theme tokens, 5-tab nav, Google Fonts (Fraunces + Inter), Room stub |
| 1 | Data layer | done | 2026-04-26 — entities, DAOs, FTS4, converters, seeder, 153 crops + 12 pests + 8 diseases JSON, Robolectric SeederTest |
| 2 | Home / This Month | done | 2026-04-26 |
| 3 | Crop list + detail | done | 2026-04-26 — grid + filter chips, detail with timeline / facts / accordions, nav from Home + Crops |
| 4 | Calendar | done | 2026-04-26 — 12-month grid (3 cols), MonthCard with S/T/H badges, month detail Seed/Seedling/Harvest tabs |
| 5 | Pest + Disease guide | done | 2026-04-26 |
| 6 | Search | done | 2026-04-26 |
| 7 | My garden + tasks | done | 2026-04-27 — add planting flow, auto-tasks, daily 08:00 reminder via WorkManager |
| 8 | Videos + polish | not started | — |
