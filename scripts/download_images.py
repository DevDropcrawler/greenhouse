"""
Keeley's Greenhouse — Wikimedia Commons image downloader
Downloads hero + thumbnail images for every crop, pest, and disease.

Usage:
    pip install requests pillow tqdm
    python scripts/download_images.py

Output:
    app/src/main/res/drawable-nodpi/crop_*.webp
    app/src/main/res/drawable-nodpi/thumb_*.webp
    app/src/main/res/drawable-nodpi/pest_*.webp
    app/src/main/res/drawable-nodpi/disease_*.webp
    scripts/image_credits.csv
"""

import json
import os
import re
import csv
import time
import requests
from pathlib import Path
from PIL import Image
from io import BytesIO
from tqdm import tqdm

# ── Config ────────────────────────────────────────────────────────────────────
PROJECT_ROOT = Path(__file__).parent.parent
DRAWABLE_DIR = PROJECT_ROOT / "app/src/main/res/drawable-nodpi"
CREDITS_FILE = PROJECT_ROOT / "scripts/image_credits.csv"
SEED_DIR = PROJECT_ROOT / "app/src/main/assets/seed"

HERO_SIZE = (1200, 800)
THUMB_SIZE = (400, 300)
WEBP_QUALITY = 80

WIKIMEDIA_API = "https://en.wikipedia.org/w/api.php"
COMMONS_API = "https://commons.wikimedia.org/w/api.php"

HEADERS = {
    "User-Agent": "KeleysGreenhouseApp/1.0 (https://github.com/waveformlabs/greenhouse; hello.waveformlabs@gmail.com)",
    "Accept": "image/webp,image/jpeg,image/png,image/*;q=0.8,*/*;q=0.5",
}

IMAGE_EXTS = (".jpg", ".jpeg", ".png", ".webp", ".gif", ".tif", ".tiff")

# ── Helpers ───────────────────────────────────────────────────────────────────

def snake_case(name: str) -> str:
    """Convert 'Tomato Money Maker' → 'tomato_money_maker'"""
    name = re.sub(r"[''\"()./]", "", name.lower())
    name = re.sub(r"[\s\-–]+", "_", name.strip())
    name = re.sub(r"[^a-z0-9_]", "", name)
    return name[:50]  # cap length for drawable name limits


def search_wikimedia(query: str) -> dict | None:
    """Search Wikimedia Commons for an image matching the query."""
    try:
        # Step 1: search Commons for a relevant file
        r = requests.get(COMMONS_API, params={
            "action": "query",
            "list": "search",
            "srsearch": f"{query} plant",
            "srnamespace": "6",  # File namespace
            "srlimit": "5",
            "format": "json",
        }, headers=HEADERS, timeout=10)
        data = r.json()
        results = data.get("query", {}).get("search", [])
        # Filter out non-image files (PDFs, OGV, etc.)
        results = [r for r in results if r["title"].lower().endswith(IMAGE_EXTS)]
        if not results:
            return search_wikipedia_image(query)

        # Try each result until one works (image with good URL)
        for r in results:
            info = get_commons_image_info(r["title"])
            if info and info["url"].lower().endswith(IMAGE_EXTS):
                return info
        return search_wikipedia_image(query)

    except Exception as e:
        print(f"  ⚠ Wikimedia search failed for '{query}': {e}")
        return None


def search_wikipedia_image(query: str) -> dict | None:
    """Fallback: get the main image from the Wikipedia article."""
    try:
        r = requests.get(WIKIMEDIA_API, params={
            "action": "query",
            "titles": query,
            "prop": "pageimages",
            "piprop": "original",
            "format": "json",
        }, headers=HEADERS, timeout=10)
        pages = r.json().get("query", {}).get("pages", {})
        for page in pages.values():
            original = page.get("original", {})
            url = original.get("source")
            if url:
                return {"url": url, "title": query, "license": "Wikipedia image"}
        return None
    except Exception as e:
        print(f"  ⚠ Wikipedia fallback failed for '{query}': {e}")
        return None


def get_commons_image_info(file_title: str) -> dict | None:
    """Get the direct URL and license info for a Commons file."""
    try:
        r = requests.get(COMMONS_API, params={
            "action": "query",
            "titles": file_title,
            "prop": "imageinfo",
            "iiprop": "url|extmetadata",
            "format": "json",
        }, headers=HEADERS, timeout=10)
        pages = r.json().get("query", {}).get("pages", {})
        for page in pages.values():
            info = page.get("imageinfo", [{}])[0]
            url = info.get("url")
            meta = info.get("extmetadata", {})
            license_name = meta.get("LicenseShortName", {}).get("value", "Unknown")
            artist = meta.get("Artist", {}).get("value", "Unknown")
            # Strip HTML from artist
            artist = re.sub(r"<[^>]+>", "", artist)
            if url:
                return {
                    "url": url,
                    "title": file_title,
                    "license": license_name,
                    "artist": artist,
                }
        return None
    except Exception as e:
        print(f"  ⚠ Image info failed for '{file_title}': {e}")
        return None


def download_and_export(url: str, hero_path: Path, thumb_path: Path) -> bool:
    """Download image, resize to hero and thumb, save as WebP."""
    try:
        r = requests.get(url, headers=HEADERS, timeout=20)
        r.raise_for_status()
        img = Image.open(BytesIO(r.content)).convert("RGB")

        # Hero
        hero = img.copy()
        hero.thumbnail((HERO_SIZE[0] * 2, HERO_SIZE[1] * 2), Image.LANCZOS)
        hero = crop_center(hero, HERO_SIZE)
        hero_path.parent.mkdir(parents=True, exist_ok=True)
        hero.save(hero_path, "WEBP", quality=WEBP_QUALITY)

        # Thumb
        thumb = img.copy()
        thumb.thumbnail((THUMB_SIZE[0] * 2, THUMB_SIZE[1] * 2), Image.LANCZOS)
        thumb = crop_center(thumb, THUMB_SIZE)
        thumb.save(thumb_path, "WEBP", quality=WEBP_QUALITY)

        return True
    except Exception as e:
        print(f"  ⚠ Download/export failed: {e}")
        return False


def download_hero_only(url: str, hero_path: Path) -> bool:
    """Download image and save as hero-size WebP only (no thumb)."""
    try:
        r = requests.get(url, headers=HEADERS, timeout=20)
        r.raise_for_status()
        img = Image.open(BytesIO(r.content)).convert("RGB")
        hero = img.copy()
        hero.thumbnail((HERO_SIZE[0] * 2, HERO_SIZE[1] * 2), Image.LANCZOS)
        hero = crop_center(hero, HERO_SIZE)
        hero_path.parent.mkdir(parents=True, exist_ok=True)
        hero.save(hero_path, "WEBP", quality=WEBP_QUALITY)
        return True
    except Exception as e:
        print(f"  ⚠ Download/export failed: {e}")
        return False


def crop_center(img: Image.Image, target: tuple) -> Image.Image:
    """Crop image to target aspect ratio from center."""
    tw, th = target
    iw, ih = img.size
    scale = max(tw / iw, th / ih)
    new_w = int(iw * scale)
    new_h = int(ih * scale)
    img = img.resize((new_w, new_h), Image.LANCZOS)
    left = (new_w - tw) // 2
    top = (new_h - th) // 2
    return img.crop((left, top, left + tw, top + th))


# ── Main ──────────────────────────────────────────────────────────────────────

def process_pest_damage(pests: list):
    """Download a damage/symptoms hero image for each pest (hero only, no thumb)."""
    credits, skipped, failed = [], [], []
    for pest in tqdm(pests, desc="Downloading pest damage images"):
        name = pest.get("name", "")
        res_name = pest.get("imageResName", f"pest_{snake_case(name)}")
        snake = res_name.replace("pest_", "")
        damage_path = DRAWABLE_DIR / f"pest_{snake}_damage.webp"

        if damage_path.exists():
            skipped.append(name)
            continue

        query = f"{name} plant damage symptoms"
        info = search_wikimedia(query)
        if info and download_hero_only(info["url"], damage_path):
            credits.append({
                "res_name": f"pest_{snake}_damage",
                "common_name": f"{name} (damage)",
                "source": info["title"],
                "license": info.get("license", "Unknown"),
                "artist": info.get("artist", "Unknown"),
                "url": info["url"],
            })
            print(f"  ✓ {name} damage → pest_{snake}_damage.webp")
        else:
            failed.append(name)
            print(f"  ✗ {name} damage — no image found")
        time.sleep(0.3)
    return credits, skipped, failed


def retry_failed(items: list, prefix: str, name_field: str, failed_names: list, hero_only: bool = False, suffix: str = ""):
    """Retry failed items using just the bare common name as the search query."""
    credits, still_failed = [], []
    by_name = {it.get(name_field, ""): it for it in items}
    for name in failed_names:
        item = by_name.get(name)
        if not item:
            still_failed.append(name)
            continue
        res_name = item.get("imageResName", f"{prefix}_{snake_case(name)}")
        snake = res_name.replace(f"{prefix}_", "")
        if hero_only:
            hero_path = DRAWABLE_DIR / f"{prefix}_{snake}{suffix}.webp"
            if hero_path.exists():
                continue
        else:
            hero_path = DRAWABLE_DIR / f"{res_name}.webp"
            thumb_path = DRAWABLE_DIR / f"thumb_{snake}.webp"
            if hero_path.exists() and thumb_path.exists():
                continue

        info = search_wikimedia(name)
        ok = False
        if info:
            if hero_only:
                ok = download_hero_only(info["url"], hero_path)
                cred_res = f"{prefix}_{snake}{suffix}"
                cred_name = f"{name} (damage)" if suffix == "_damage" else name
            else:
                ok = download_and_export(info["url"], hero_path, thumb_path)
                cred_res = res_name
                cred_name = name
        if ok and info:
            credits.append({
                "res_name": cred_res,
                "common_name": cred_name,
                "source": info["title"],
                "license": info.get("license", "Unknown"),
                "artist": info.get("artist", "Unknown"),
                "url": info["url"],
            })
            print(f"  ✓ retry: {name}")
        else:
            still_failed.append(name)
            print(f"  ✗ retry failed: {name}")
        time.sleep(0.3)
    return credits, still_failed


def process_items(items: list, prefix: str, name_field: str, search_suffix: str = "plant"):
    """Download images for a list of items (crops, pests, or diseases)."""
    credits = []
    skipped = []
    failed = []

    for item in tqdm(items, desc=f"Downloading {prefix} images"):
        name = item.get(name_field, "")
        res_name = item.get("imageResName", f"{prefix}_{snake_case(name)}")
        snake = res_name.replace(f"{prefix}_", "")

        hero_path = DRAWABLE_DIR / f"{res_name}.webp"
        thumb_path = DRAWABLE_DIR / f"thumb_{snake}.webp"

        # Skip if already downloaded
        if hero_path.exists() and thumb_path.exists():
            skipped.append(name)
            continue

        # Build search query
        aliases = item.get("aliases", [])
        search_query = aliases[0] if aliases else name
        search_query = f"{search_query} {search_suffix}"

        # Search and download
        image_info = search_wikimedia(search_query)
        if image_info and download_and_export(image_info["url"], hero_path, thumb_path):
            credits.append({
                "res_name": res_name,
                "common_name": name,
                "source": image_info["title"],
                "license": image_info.get("license", "Unknown"),
                "artist": image_info.get("artist", "Unknown"),
                "url": image_info["url"],
            })
            print(f"  ✓ {name} → {res_name}.webp")
        else:
            failed.append(name)
            print(f"  ✗ {name} — no image found, will use placeholder")

        time.sleep(0.3)  # Be polite to Wikimedia

    return credits, skipped, failed


def main():
    print("=" * 60)
    print("Keeley's Greenhouse — Image Downloader")
    print("=" * 60)

    DRAWABLE_DIR.mkdir(parents=True, exist_ok=True)

    all_credits = []

    # Load seed data
    with open(SEED_DIR / "crops.json", encoding="utf-8") as f:
        crops = json.load(f)
    with open(SEED_DIR / "pests.json", encoding="utf-8") as f:
        pests = json.load(f)
    with open(SEED_DIR / "diseases.json", encoding="utf-8") as f:
        diseases = json.load(f)

    print(f"\nFound: {len(crops)} crops, {len(pests)} pests, {len(diseases)} diseases")
    print(f"Output: {DRAWABLE_DIR}\n")

    # Download crops
    print("\n── CROPS ──────────────────────────────────────")
    c, s, crops_failed = process_items(crops, "crop", "commonName", "vegetable plant")
    all_credits.extend(c)
    print(f"  Done: {len(c)} downloaded, {len(s)} skipped, {len(crops_failed)} failed")

    # Download pests
    print("\n── PESTS ──────────────────────────────────────")
    c, s, pests_failed = process_items(pests, "pest", "name", "garden insect pest")
    all_credits.extend(c)
    print(f"  Done: {len(c)} downloaded, {len(s)} skipped, {len(pests_failed)} failed")

    # Download pest damage (hero only)
    print("\n── PEST DAMAGE ─────────────────────────────────")
    c, s, pest_damage_failed = process_pest_damage(pests)
    all_credits.extend(c)
    print(f"  Done: {len(c)} downloaded, {len(s)} skipped, {len(pest_damage_failed)} failed")

    # Download diseases
    print("\n── DISEASES ────────────────────────────────────")
    c, s, diseases_failed = process_items(diseases, "disease", "name", "plant disease")
    all_credits.extend(c)
    print(f"  Done: {len(c)} downloaded, {len(s)} skipped, {len(diseases_failed)} failed")

    # Retry pass — use bare common name
    print("\n── RETRY (bare common name) ────────────────────")
    rc, crops_failed2 = retry_failed(crops, "crop", "commonName", crops_failed)
    all_credits.extend(rc)
    rc, pests_failed2 = retry_failed(pests, "pest", "name", pests_failed)
    all_credits.extend(rc)
    rc, pest_damage_failed2 = retry_failed(pests, "pest", "name", pest_damage_failed, hero_only=True, suffix="_damage")
    all_credits.extend(rc)
    rc, diseases_failed2 = retry_failed(diseases, "disease", "name", diseases_failed)
    all_credits.extend(rc)

    print("\n── FINAL FAILURES ──────────────────────────────")
    print(f"  Crops failed:        {crops_failed2}")
    print(f"  Pests failed:        {pests_failed2}")
    print(f"  Pest damage failed:  {pest_damage_failed2}")
    print(f"  Diseases failed:     {diseases_failed2}")

    # Write credits CSV
    CREDITS_FILE.parent.mkdir(parents=True, exist_ok=True)
    with open(CREDITS_FILE, "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=[
            "res_name", "common_name", "source", "license", "artist", "url"
        ])
        writer.writeheader()
        writer.writerows(all_credits)

    print(f"\n✓ Credits saved to {CREDITS_FILE}")
    print(f"✓ {len(all_credits)} images downloaded total")
    print("\nDone! Now run the app to see images.")
    print("Any 'failed' items will show the olive placeholder.")
    print("You can manually replace any image by dropping a")
    print(".webp file into app/src/main/res/drawable-nodpi/")


if __name__ == "__main__":
    main()