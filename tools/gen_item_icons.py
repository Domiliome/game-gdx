"""Generate 32x32 pixel-art item icons (white outline, style like backpack.png)."""
from PIL import Image

BG = (0, 0, 0, 0)
OUT = (255, 255, 255, 255)
BLACK = (20, 20, 24, 255)


def blank():
    return Image.new("RGBA", (32, 32), BG)


def put(im, x, y, c):
    if 0 <= x < 32 and 0 <= y < 32:
        im.putpixel((x, y), c)


def fill(im, pts, c):
    for x, y in pts:
        put(im, x, y, c)


def outline_neighbors(im, solid_color_check):
    """Add white outline around opaque non-outline pixels."""
    pixels = im.load()
    to_add = []
    for y in range(32):
        for x in range(32):
            if pixels[x, y][3] == 0:
                for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < 32 and 0 <= ny < 32:
                        n = pixels[nx, ny]
                        if n[3] > 0 and n != OUT:
                            to_add.append((x, y))
                            break
    for x, y in to_add:
        put(im, x, y, OUT)


def rusty_arrow():
    im = blank()
    shaft = (160, 100, 55, 255)
    tip = (180, 180, 190, 255)
    tip_d = (110, 110, 120, 255)
    fletch = (140, 60, 40, 255)
    # shaft
    for x in range(6, 24):
        put(im, x, 15, shaft)
        put(im, x, 16, shaft)
    # tip
    put(im, 24, 14, tip_d); put(im, 25, 14, tip)
    put(im, 24, 15, tip); put(im, 25, 15, tip); put(im, 26, 15, tip)
    put(im, 24, 16, tip); put(im, 25, 16, tip); put(im, 26, 16, tip)
    put(im, 24, 17, tip_d); put(im, 25, 17, tip)
    put(im, 27, 15, tip); put(im, 27, 16, tip_d)
    # fletching
    put(im, 5, 13, fletch); put(im, 6, 14, fletch)
    put(im, 5, 18, fletch); put(im, 6, 17, fletch)
    put(im, 4, 15, fletch); put(im, 4, 16, fletch)
    outline_neighbors(im, None)
    return im


def spring_arrow():
    im = blank()
    shaft = (70, 170, 90, 255)
    tip = (240, 210, 70, 255)
    tip_d = (180, 140, 30, 255)
    spring = (200, 220, 230, 255)
    for x in range(7, 22):
        put(im, x, 15, shaft)
        put(im, x, 16, shaft)
    # spring coils
    for x in (9, 11, 13, 15):
        put(im, x, 14, spring)
        put(im, x + 1, 17, spring)
    put(im, 22, 14, tip_d); put(im, 23, 14, tip)
    put(im, 22, 15, tip); put(im, 23, 15, tip); put(im, 24, 15, tip)
    put(im, 22, 16, tip); put(im, 23, 16, tip); put(im, 24, 16, tip)
    put(im, 22, 17, tip_d); put(im, 23, 17, tip)
    put(im, 25, 15, tip); put(im, 25, 16, tip_d)
    put(im, 5, 13, shaft); put(im, 6, 14, shaft)
    put(im, 5, 18, shaft); put(im, 6, 17, shaft)
    outline_neighbors(im, None)
    return im


def heavy_core():
    im = blank()
    dark = (70, 75, 85, 255)
    mid = (120, 125, 135, 255)
    light = (190, 195, 205, 255)
    accent = (90, 200, 120, 255)
    cx, cy, r = 15, 15, 9
    for y in range(32):
        for x in range(32):
            dx, dy = x - cx, y - cy
            if dx * dx + dy * dy <= r * r:
                # shading
                if dx + dy < -4:
                    put(im, x, y, light)
                elif dx + dy > 6:
                    put(im, x, y, dark)
                else:
                    put(im, x, y, mid)
    # diamond accent
    put(im, 15, 13, accent)
    put(im, 14, 14, accent); put(im, 15, 14, accent); put(im, 16, 14, accent)
    put(im, 15, 15, accent)
    outline_neighbors(im, None)
    return im


def mana_crystal():
    im = blank()
    dark = (50, 40, 140, 255)
    mid = (90, 110, 230, 255)
    light = (180, 200, 255, 255)
    glow = (220, 160, 255, 255)
    # diamond crystal
    pts = {
        (15, 6): light, (14, 7): light, (15, 7): light, (16, 7): light,
        (13, 8): mid, (14, 8): light, (15, 8): light, (16, 8): light, (17, 8): mid,
        (12, 9): mid, (13, 9): mid, (14, 9): mid, (15, 9): light, (16, 9): mid, (17, 9): mid, (18, 9): mid,
        (11, 10): dark, (12, 10): mid, (13, 10): mid, (14, 10): glow, (15, 10): light,
        (16, 10): mid, (17, 10): mid, (18, 10): mid, (19, 10): dark,
        (11, 11): dark, (12, 11): mid, (13, 11): glow, (14, 11): light, (15, 11): light,
        (16, 11): mid, (17, 11): mid, (18, 11): mid, (19, 11): dark,
        (11, 12): dark, (12, 12): mid, (13, 12): mid, (14, 12): mid, (15, 12): glow,
        (16, 12): mid, (17, 12): mid, (18, 12): mid, (19, 12): dark,
        (12, 13): mid, (13, 13): mid, (14, 13): mid, (15, 13): mid, (16, 13): mid, (17, 13): mid, (18, 13): mid,
        (12, 14): dark, (13, 14): mid, (14, 14): mid, (15, 14): mid, (16, 14): mid, (17, 14): mid, (18, 14): dark,
        (13, 15): dark, (14, 15): mid, (15, 15): mid, (16, 15): mid, (17, 15): dark,
        (13, 16): dark, (14, 16): mid, (15, 16): mid, (16, 16): dark,
        (14, 17): dark, (15, 17): mid, (16, 17): dark,
        (14, 18): dark, (15, 18): dark,
        (15, 19): dark,
    }
    for (x, y), c in pts.items():
        put(im, x, y, c)
    outline_neighbors(im, None)
    return im


def main():
    assets = r"c:\Users\E1000\github-projects\game-gdx\assets\items"
    mapping = {
        "rusty_arrow.png": rusty_arrow,
        "spring_arrow.png": spring_arrow,
        "heavy_core.png": heavy_core,
        "mana_crystal.png": mana_crystal,
    }
    for name, fn in mapping.items():
        path = f"{assets}/{name}"
        fn().save(path)
        print("saved", path)


if __name__ == "__main__":
    main()
