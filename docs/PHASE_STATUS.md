# Phase Status

## Active phase
**Phase 1 — Data layer**

## Phase 1 goals
- Define Room entities for Crop, Pest, Disease, UserPlanting, Task, Favourite (plan §4.1)
- Define DAOs for each entity
- Add FTS4 virtual tables: CropFts, PestFts, DiseaseFts (plan §4.2)
- Replace the Phase-0 `Phase0Stub` entity in `data/AppDatabase.kt` with the real entity set, version 1
- Implement first-launch seeder reading from `app/src/main/assets/seed/*.json`
- Ship seed JSON for all 153 crops/fruit trees + 12 pests + 8 diseases (plan §11–§13)
- DAO + seeder unit tests pass

## Done when
- `./gradlew assembleDebug` is green
- DAO and seeder tests pass
- App boots and seeds DB on first launch (verify via logcat)
- Zero new lint errors
- Screenshot at `docs/screenshots/phase-1.png`
- Commit message: `phase 1: data layer`

## Phase history

| # | Phase | Status | Notes |
|---|---|---|---|
| 0 | Skeleton | done | 2026-04-26 — project, Hilt app, empty scaffold, version catalog, light theme tokens, 5-tab nav, Google Fonts (Fraunces + Inter), Room stub |
| 1 | Data layer | active | — |
| 2 | Home / This Month | not started | — |
| 3 | Crop list + detail | not started | — |
| 4 | Calendar | not started | — |
| 5 | Pest + Disease guide | not started | — |
| 6 | Search | not started | — |
| 7 | My garden + tasks | not started | — |
| 8 | Videos + polish | not started | — |
