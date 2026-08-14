from PIL import Image

BG = (0, 0, 0, 0)
CORNER = (70, 90, 110, 255)
BODY = (40, 80, 140, 255)
BODY_DARK = (20, 45, 90, 255)
CYAN = (80, 220, 255, 255)
CYAN_DARK = (30, 140, 200, 255)
WHITE = (240, 250, 255, 255)
YELLOW = (255, 240, 120, 255)


def blank():
    return Image.new("RGBA", (32, 32), BG)


def put(im, x, y, c):
    if 0 <= x < 32 and 0 <= y < 32:
        im.putpixel((x, y), c)


def draw_corners(im, color=CORNER):
    for ox, oy in [(9, 9), (20, 9), (9, 20), (20, 20)]:
        for dx in range(3):
            for dy in range(3):
                put(im, ox + dx, oy + dy, color)


def draw_body(im, color=BODY):
    for x in range(11, 21):
        for y in range(11, 21):
            put(im, x, y, color)


def draw_rim(im):
    for x in range(11, 21):
        put(im, x, 11, BODY_DARK)
        put(im, x, 20, BODY_DARK)
    for y in range(11, 21):
        put(im, 11, y, BODY_DARK)
        put(im, 20, y, BODY_DARK)


def draw_bolt(im):
    # Zigzag lightning in the center
    put(im, 16, 12, CYAN)
    put(im, 15, 13, CYAN)
    put(im, 16, 13, WHITE)
    put(im, 17, 13, CYAN)
    put(im, 14, 14, CYAN_DARK)
    put(im, 15, 14, CYAN)
    put(im, 16, 14, YELLOW)
    put(im, 15, 15, WHITE)
    put(im, 16, 15, CYAN)
    put(im, 17, 15, CYAN)
    put(im, 16, 16, YELLOW)
    put(im, 17, 16, CYAN)
    put(im, 15, 17, CYAN)
    put(im, 16, 17, WHITE)
    put(im, 17, 17, CYAN_DARK)
    put(im, 16, 18, CYAN)
    put(im, 15, 19, CYAN_DARK)


def idle():
    im = blank()
    draw_corners(im)
    draw_body(im)
    draw_rim(im)
    draw_bolt(im)
    return im


def make_frame(i):
    im = blank()
    if i == 0:
        put(im, 15, 15, CORNER)
        put(im, 16, 15, CORNER)
        put(im, 15, 16, CORNER)
        put(im, 16, 16, CORNER)
    elif i == 1:
        for x in range(14, 18):
            for y in range(14, 18):
                put(im, x, y, CORNER)
    elif i == 2:
        for x in range(13, 19):
            for y in range(13, 19):
                put(im, x, y, CORNER)
    elif i == 3:
        for x in range(12, 20):
            for y in range(12, 20):
                put(im, x, y, CORNER)
    elif i == 4:
        draw_corners(im, CORNER)
        for x in range(11, 21):
            for y in range(11, 21):
                put(im, x, y, CORNER)
    elif i == 5:
        draw_corners(im)
        draw_body(im, BODY_DARK)
    elif i == 6:
        draw_corners(im)
        draw_body(im, BODY)
    elif i == 7:
        draw_corners(im)
        draw_body(im)
        put(im, 15, 15, CYAN_DARK)
        put(im, 16, 15, CYAN_DARK)
        put(im, 15, 16, CYAN_DARK)
        put(im, 16, 16, CYAN_DARK)
    elif i == 8:
        draw_corners(im)
        draw_body(im)
        put(im, 16, 14, CYAN)
        put(im, 15, 15, CYAN)
        put(im, 16, 15, WHITE)
        put(im, 16, 16, CYAN)
        put(im, 17, 17, CYAN)
    elif i == 9:
        draw_corners(im)
        draw_body(im)
        draw_rim(im)
        put(im, 16, 13, CYAN)
        put(im, 15, 14, CYAN)
        put(im, 16, 15, YELLOW)
        put(im, 17, 16, CYAN)
        put(im, 16, 17, CYAN)
    elif i == 10:
        draw_corners(im)
        draw_body(im)
        draw_rim(im)
        draw_bolt(im)
    else:
        return idle()
    return im


def main():
    assets = r"c:\Users\E1000\github-projects\game-gdx\assets\towers"
    idle().save(f"{assets}/tesla.png")

    frames = [make_frame(i) for i in range(13)]
    strip = Image.new("RGBA", (32 * len(frames), 32), BG)
    for idx, fr in enumerate(frames):
        strip.paste(fr, (idx * 32, 0))
    strip.save(f"{assets}/tesla_init.png")
    print("saved", len(frames), "frames", strip.size)


if __name__ == "__main__":
    main()
