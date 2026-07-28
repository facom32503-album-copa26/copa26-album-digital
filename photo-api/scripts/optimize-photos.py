#!/usr/bin/env python3
"""Reduz as fotos baixadas para WebP 320px, preservando a transparencia do recorte.

Os recortes do TheSportsDB vem em PNG 500x500 (~230 KB cada), pesado demais para
servir por CDN. Em WebP 320px a media cai para ~15 KB sem perda visivel nos
tamanhos em que o app exibe (56dp na grade, 120dp no detalhe).

Uso: python3 scripts/optimize-photos.py
Requer: Pillow (pip install Pillow)
"""
import glob
import os
import sys

try:
    from PIL import Image
except ImportError:
    sys.exit("Pillow nao instalado. Rode: pip install Pillow")

MAX_SIDE = 320
QUALITY = 82
ROOT = os.path.join(os.path.dirname(__file__), "..", "photos")


def main() -> None:
    before = after = count = 0
    for kind in ("players", "coaches"):
        for path in glob.glob(os.path.join(ROOT, kind, "*")):
            if path.endswith((".gitkeep", ".webp")):
                continue
            before += os.path.getsize(path)
            image = Image.open(path).convert("RGBA")
            image.thumbnail((MAX_SIDE, MAX_SIDE), Image.LANCZOS)
            target = os.path.splitext(path)[0] + ".webp"
            image.save(target, "WEBP", quality=QUALITY, method=6)
            after += os.path.getsize(target)
            os.remove(path)
            count += 1

    if count == 0:
        print("Nada a converter (todas as fotos ja estao em .webp).")
        return
    print(f"{count} fotos convertidas")
    print(f"{before / 1048576:.1f} MB -> {after / 1048576:.2f} MB "
          f"(media {after / count / 1024:.1f} KB por foto)")


if __name__ == "__main__":
    main()
