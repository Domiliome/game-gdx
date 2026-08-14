"""Generate 32x32 walk-cycle sprite sheets for enemies (4 frames, horizontal strip)."""
import os
from PIL import Image

BG = (0, 0, 0, 0)
OUT = (255, 255, 255, 255)
FRAME_W = 32
FRAME_H = 32
FRAMES = 4


def blank_frame():
    return Image.new("RGBA", (FRAME_W, FRAME_H), BG)


def sheet(frames):
    im = Image.new("RGBA", (FRAME_W * len(frames), FRAME_H), BG)
    for i, frame in enumerate(frames):
        im.paste(frame, (i * FRAME_W, 0))
    return im


def put(im, x, y, c):
    if 0 <= x < FRAME_W and 0 <= y < FRAME_H:
        im.putpixel((x, y), c)


def outline(im):
    px = im.load()
    add = []
    for y in range(FRAME_H):
        for x in range(FRAME_W):
            if px[x, y][3] == 0:
                for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < FRAME_W and 0 <= ny < FRAME_H:
                        n = px[nx, ny]
                        if n[3] > 0 and n != OUT:
                            add.append((x, y))
                            break
    for x, y in add:
        put(im, x, y, OUT)


def draw_goblin_body(im, ox, oy, leg=0):
    """Small green goblin facing down; leg: -1 left, 0 neutral, 1 right."""
    skin = (120, 210, 70, 255)
    skin_d = (55, 130, 35, 255)
    ear = (90, 180, 55, 255)
    eye = (30, 40, 25, 255)
    sword = (235, 240, 245, 255)
    sword_d = (170, 175, 185, 255)

    for dy in range(3):
        put(im, ox + 10, oy + 8 - dy, ear)
        put(im, ox + 21, oy + 8 - dy, ear)
    for y in range(9, 20):
        for x in range(ox + 10, ox + 22):
            if (x - ox - 16) ** 2 + (y - 14) ** 2 <= 30:
                put(im, x, y, skin if y < 15 else skin_d)
    put(im, ox + 13, oy + 12, eye)
    put(im, ox + 18, oy + 12, eye)
    put(im, ox + 15, oy + 14, skin_d)
    put(im, ox + 16, oy + 14, skin_d)
    lx = ox + 12 + (1 if leg == -1 else 0)
    rx = ox + 18 + (1 if leg == 1 else 0)
    for y in range(19, 24):
        put(im, lx, y, skin_d)
        put(im, rx, y, skin_d)
    put(im, ox + 22, oy + 11, sword_d)
    put(im, ox + 23, oy + 11, sword)
    for y in range(12, 22):
        put(im, ox + 22, y, sword_d)
        put(im, ox + 23, y, sword)
    put(im, ox + 21, oy + 21, sword_d)
    put(im, ox + 22, oy + 22, sword)
    put(im, ox + 23, oy + 22, sword)
    outline(im)
    return im


def goblin_frames():
    return [
        draw_goblin_body(blank_frame(), 0, 0, leg=-1),
        draw_goblin_body(blank_frame(), 0, 0, leg=0),
        draw_goblin_body(blank_frame(), 0, 0, leg=1),
        draw_goblin_body(blank_frame(), 0, 0, leg=0),
    ]


def draw_zombie_body(im, ox, oy, arm=0, bob=0):
    """Shambling zombie: gray-green, torn shirt, arms out."""
    skin = (95, 130, 85, 255)
    skin_d = (55, 80, 60, 255)
    cloth = (110, 85, 65, 255)
    cloth_d = (70, 50, 40, 255)
    eye = (180, 40, 40, 255)
    oy += bob

    # head
    for y in range(7 + oy, 14 + oy):
        for x in range(ox + 11, ox + 21):
            put(im, x, y, skin if y < 11 + oy else skin_d)
    put(im, ox + 13, oy + 9, eye)
    put(im, ox + 18, oy + 9, eye)
    put(im, ox + 14, oy + 11, skin_d)
    put(im, ox + 17, oy + 11, skin_d)
    # torso
    for y in range(14 + oy, 22 + oy):
        for x in range(ox + 10, ox + 22):
            put(im, x, y, cloth if (x + y) % 3 else cloth_d)
    # arms
    ay = oy + 15 + arm
    for x in range(ox + 6, ox + 10):
        put(im, x, ay, skin)
        put(im, x, ay + 1, skin_d)
    for x in range(ox + 22, ox + 26):
        put(im, x, ay + 1, skin)
        put(im, x, ay + 2, skin_d)
    # legs shuffle
    for y in range(22 + oy, 27 + oy):
        put(im, ox + 12 + arm, y, skin_d)
        put(im, ox + 18 - arm, y, skin_d)
    outline(im)
    return im


def zombie_frames():
    return [
        draw_zombie_body(blank_frame(), 0, 0, arm=0, bob=0),
        draw_zombie_body(blank_frame(), 0, 0, arm=1, bob=1),
        draw_zombie_body(blank_frame(), 0, 0, arm=0, bob=0),
        draw_zombie_body(blank_frame(), 0, 0, arm=-1, bob=1),
    ]


def draw_orc_body(im, ox, oy, stomp=0, axe=0):
    """Heavy orc with armor plates and axe."""
    skin = (95, 145, 65, 255)
    skin_d = (55, 95, 35, 255)
    armor = (75, 80, 90, 255)
    armor_l = (130, 135, 145, 255)
    tusk = (230, 225, 210, 255)
    axe_w = (180, 185, 195, 255)
    axe_b = (120, 70, 35, 255)
    oy += stomp

    # big head
    for y in range(6 + oy, 15 + oy):
        for x in range(ox + 9, ox + 23):
            put(im, x, y, skin if y < 11 + oy else skin_d)
    put(im, ox + 12, oy + 9, (25, 30, 20, 255))
    put(im, ox + 19, oy + 9, (25, 30, 20, 255))
    put(im, ox + 10, oy + 12, tusk)
    put(im, ox + 21, oy + 12, tusk)
    # chest armor
    for y in range(15 + oy, 23 + oy):
        for x in range(ox + 8, ox + 24):
            c = armor_l if x < ox + 14 else armor
            put(im, x, y, c)
    put(im, ox + 15, oy + 18, armor_l)
    put(im, ox + 16, oy + 18, armor_l)
    # legs
    for y in range(23 + oy, 28 + oy):
        put(im, ox + 11 + stomp, y, skin_d)
        put(im, ox + 18 - stomp, y, skin_d)
    # axe
    ax = ox + 22 + axe
    for y in range(10 + oy, 24 + oy):
        put(im, ax, y, axe_b)
    put(im, ax - 1, oy + 9, axe_w)
    put(im, ax, oy + 8, axe_w)
    put(im, ax + 1, oy + 8, axe_w)
    put(im, ax, oy + 7, axe_w)
    outline(im)
    return im


def orc_frames():
    return [
        draw_orc_body(blank_frame(), 0, 0, stomp=0, axe=0),
        draw_orc_body(blank_frame(), 0, 0, stomp=1, axe=0),
        draw_orc_body(blank_frame(), 0, 0, stomp=0, axe=1),
        draw_orc_body(blank_frame(), 0, 0, stomp=1, axe=0),
    ]


def main():
    assets = r"c:\Users\E1000\github-projects\game-gdx\assets"
    mapping = {
        "enemies/goblin.png": goblin_frames,
        "enemies/zombie.png": zombie_frames,
        "enemies/orc.png": orc_frames,
    }
    for name, fn in mapping.items():
        path = f"{assets}/{name}"
        os.makedirs(os.path.dirname(path), exist_ok=True)
        sheet(fn()).save(path)
        print("saved", path)


if __name__ == "__main__":
    main()
