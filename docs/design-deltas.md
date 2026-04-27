# design-deltas.md

Drift log. The agent records every decision made when building a screen marked **Tokens** in `docs/design-coverage.md` (i.e. no HTML reference exists).

Each entry helps the project owner audit choices and helps a future post-v1 design pass close the gaps.

## Entry template

```
### YYYY-MM-DD · Screen name · Phase N

**Type:** Tokens build (no HTML reference)
**Closest reference frame:** XX-frame-name
**Decisions taken:**
- decision 1
- decision 2
**Notes for future design pass:**
- thing to revisit
```

## Entries

### 2026-04-27 · Settings screen · Phase 8

**Type:** HTML reference (frame 13) + token decisions
**Closest reference frame:** 13-standardized-header-system-settings-screen.html
**Decisions taken:**
- Sections: Notifications, Data, About — three eyebrow-labelled groups with brass divider rows.
- Daily reminder time uses Material 3 `TimePicker` inside an `AlertDialog`. No custom dial design.
- Reset-data row uses an `AlertDialog` confirmation; destructive label rendered in `AppError` red.
- No dark-mode toggle in v1 (plan §5: light only). The frame's dark-mode row is omitted.
- No notification-time custom modal (frame omitted); reuse stock M3 dialog to keep Phase 8 scope small.
- Reminder time persisted in `SharedPreferences` (`SettingsStore`) rather than DataStore — avoids a new dependency.
- Data version row is hard-coded `"v1.0 · 2026-04"` for v1; future releases can bump this.

**Notes for future design pass:**
- Build a custom token-styled time picker (olive header, brass numerals).
- Add per-pest / per-crop notification toggles.
- Consider replacing About text with a scrollable credit list once we have CC-BY photo attributions in `art-source/CREDITS.csv`.

### 2026-04-27 · Video card / VideoSection · Phase 8

**Type:** Tokens build (no dedicated HTML reference)
**Closest reference frame:** 08-standardized-header-for-tomato-detail.html ("Watch & learn" pattern)
**Decisions taken:**
- Curated video card: 120×72 dp thumbnail (Coil → `img.youtube.com/vi/<id>/mqdefault.jpg`), forest play badge overlay, "Watch on YouTube" / "Tap to play in YouTube" body text, 22 dp radius, 0.5 dp brass border.
- "More on YouTube" pill button under the cards triggers `Intent.ACTION_VIEW` → `youtube.com/results?search_query=...`.
- Card tap → `Intent.ACTION_VIEW` with `vnd.youtube:<id>` (YouTube app), falling back to `youtube.com/watch?v=<id>` if the app isn't installed (per plan §6).
- Used on Crop, Pest, and Disease detail screens; "Videos" accordion now opens by default since it's an actionable section.
- INTERNET permission added to manifest **only** for thumbnail loading (no other network use).

**Notes for future design pass:**
- Curate `youtubeVideoIds` per crop/pest/disease in a future seed pass (currently empty in JSON; only the search fallback runs).
- Add a duration chip and channel name once curation is done — DTO already supports the fields.
- Consider an offline placeholder thumbnail (`drawable/video_placeholder.webp`).

### 2026-04-27 · Favourites · Phase 8

**Type:** Scope decision
**Closest reference frame:** none (plan §9 lists screen 25)
**Decisions taken:**
- **Deferred to post-v1.** `FavouriteDao` exists from Phase 1 but no UI ships in v1.
- More screen does not include a Favourites row; nav graph has no Favourites route.
- Rationale: v1 done-criteria (plan §15) do not require Favourites; ship without to reduce surface area.

**Notes for future design pass:**
- Build screen patterned on Crops list (per design-coverage.md), wired to `FavouriteDao`.
- Add a heart icon to crop / pest / disease detail app bars to toggle favourite state.
