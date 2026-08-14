from PIL import Image

BG = (0, 0, 0, 0)
CORNER = (90, 80, 100, 255)
BODY = (120, 70, 160, 255)
BODY_DARK = (70, 40, 95, 255)
LIME = (140, 230, 70, 255)
LIME_DARK = (70, 140, 30, 255)
WHITE = (230, 255, 200, 255)


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


def draw_droplet(im, fill=LIME, outline=LIME_DARK, tip_y=13):
    put(im, 15, tip_y, outline)
    put(im, 16, tip_y, outline)
    put(im, 14, tip_y + 1, outline)
    put(im, 15, tip_y + 1, fill)
    put(im, 16, tip_y + 1, fill)
    put(im, 17, tip_y + 1, outline)
    put(im, 13, tip_y + 2, outline)
    for x in range(14, 18):
        put(im, x, tip_y + 2, fill)
    put(im, 18, tip_y + 2, outline)
    put(im, 13, tip_y + 3, outline)
    for x in range(14, 18):
        put(im, x, tip_y + 3, fill)
    put(im, 18, tip_y + 3, outline)
    put(im, 14, tip_y + 4, outline)
    put(im, 15, tip_y + 4, fill)
    put(im, 16, tip_y + 4, fill)
    put(im, 17, tip_y + 4, outline)
    put(im, 15, tip_y + 5, outline)
    put(im, 16, tip_y + 5, outline)
    put(im, 15, tip_y + 2, WHITE)


def idle():
    im = blank()
    draw_corners(im)
    draw_body(im)
    draw_rim(im)
    draw_droplet(im)
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
        put(im, 15, 15, LIME_DARK)
        put(im, 16, 15, LIME_DARK)
        put(im, 15, 16, LIME_DARK)
        put(im, 16, 16, LIME_DARK)
    elif i == 8:
        draw_corners(im)
        draw_body(im)
        put(im, 15, 14, LIME)
        put(im, 16, 14, LIME)
        for x in range(14, 18):
            put(im, x, 15, LIME)
            put(im, x, 16, LIME)
        put(im, 15, 17, LIME)
        put(im, 16, 17, LIME)
    elif i == 9:
        draw_corners(im)
        draw_body(im)
        draw_droplet(im, tip_y=14)
    elif i == 10:
        draw_corners(im)
        draw_body(im)
        draw_rim(im)
        draw_droplet(im, tip_y=13)
    else:
        return idle()
    return im


def main():
    assets = r"c:\Users\E1000\github-projects\game-gdx\assets\towers"
    idle().save(f"{assets}/poison.png")

    frames = [make_frame(i) for i in range(13)]
    strip = Image.new("RGBA", (32 * len(frames), 32), BG)
    for idx, fr in enumerate(frames):
        strip.paste(fr, (idx * 32, 0))
    strip.save(f"{assets}/poison_init.png")
    print("saved", len(frames), "frames", strip.size)


if __name__ == "__main__":
    main()
