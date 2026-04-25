# AGENTS.md — read this first, every session

You are building **Keeley's Greenhouse**, an offline Android app for home greenhouse growers in Papamoa, New Zealand. This file is the contract between you and the project owner. Do not deviate from it.

## Required reading order, every session

1. `docs/AGENTS.md` (this file)
2. `docs/PAPAMOA_GREENHOUSE_APP_PLAN_2.md` (the spec — call it "the plan" below)
3. `docs/PHASE_STATUS.md` (which phase is active)
4. `docs/design-coverage.md` (which design HTML exists for which screen)
5. `assets/superdesignhtml/` (design reference — read the relevant screen's HTML before building it)

If any of these are missing or contradictory, **stop and ask the project owner**. Do not guess.

## Hard rules

1. **One phase per session.** The active phase is in `docs/PHASE_STATUS.md`. Do everything in that phase, commit `phase N: <description>`, then stop. Do not start the next phase.
2. **Build green after every change.** Run `./gradlew assembleDebug`. If it fails, fix it before adding more code.
3. **Seed data lives in JSON, never in Kotlin.** Crops, pests, diseases come from `app/src/main/assets/seed/*.json`. If a fact needs editing, edit the JSON.
4. **No new libraries** without a one-line justification in the commit message. Prefer the version catalog (`gradle/libs.versions.toml`).
5. **No hard-coded UI strings.** Everything goes in `res/values/strings.xml`.
6. **No `INTERNET` permission** except for loading YouTube thumbnails (this is the only allowed network use).
7. **No Firebase, no analytics, no cloud, no auth.** Offline-first means offline.
8. **Do not invent crops, pests, dosages, or NZ-specific facts.** Use only what's in the plan or seed files. If something is missing, ask.
9. **Match the design baseline.** Reference HTML lives in `assets/superdesignhtml/`. Read the relevant file before building a screen. If a design is missing for a screen, build a placeholder using the design tokens in `Theme.kt` and log it in `docs/design-deltas.md`.
10. **Images are bundled WebP.** Drop sources in `art-source/` (gitignored), export to `app/src/main/res/drawable-nodpi/` named `crop_<snake>`, `pest_<snake>`, `disease_<snake>`. Each ≤200 KB.
11. **`./gradlew verifyArt` must pass.** Every crop/pest/disease in seed JSON has matching drawables, else build fails.
12. **Sticky bottom nav, hidden on details.** Use `Scaffold(bottomBar = …)`; main lists pad bottom by 72 dp + nav inset; detail screens swap nav for a sticky action bar.
13. **Tests per phase.** See plan §7. Never skip them.
14. **Commits:** small, present-tense, no emojis. One commit can contain multiple files but must build green.

## Definition of done — per phase

- All tests pass.
- App installs on a real device.
- Zero new lint errors.
- Screenshot saved to `docs/screenshots/phase-N.png`.
- `docs/PHASE_STATUS.md` updated by the agent at end of session.

## Definition of done — v1

See plan §15.

## When you don't know something

Ask. Do not guess about climate facts, NZ horticultural practices, or which pest control is registered in NZ. The plan has been researched; if it isn't in the plan, it isn't decided.

## When the design is unclear

`docs/design-coverage.md` tells you the source for every screen — either an HTML file in `assets/superdesignhtml/`, or a "Tokens build" using `Theme.kt` and the visual conventions established by the 25 existing HTML frames.

For any **Tokens build**:
1. Read the closest reference frame listed in `design-coverage.md`.
2. Reuse colours, spacing, type, and component patterns from `Theme.kt` and the reference frame. Do not invent new colours, paddings, or shapes.
3. Append an entry to `docs/design-deltas.md` recording the screen name, reference frame, and decisions taken.

If the plan and the HTML conflict: the plan wins for behaviour, the HTML wins for visual.

## Out-of-scope screens (do NOT build in v1)

- Video Player (we use YouTube intents instead)
- Greenhouse Monitor (sensor integration is post-v1)
- Cloud sync, multi-user, sharing
- Inline YouTube player

If a design HTML exists for one of these, ignore it.

## How to handle ambiguity

1. Re-read the plan section that covers the topic.
2. Check `docs/design-deltas.md` for prior decisions.
3. Ask the project owner. Do not improvise.
