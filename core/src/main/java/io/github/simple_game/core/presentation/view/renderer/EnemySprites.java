package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

/** Кэш анимаций врагов — одна текстура на тип, а не на каждый инстанс. */
public final class EnemySprites {
    private static final int FRAME_SIZE = 32;
    private static final float FRAME_DURATION = 0.08f;
    private static final ObjectMap<String, Animation<TextureRegion>> CACHE = new ObjectMap<>();

    private EnemySprites() {}

    public static Animation<TextureRegion> runAnimation(String texturePath) {
        Animation<TextureRegion> cached = CACHE.get(texturePath);
        if (cached != null) {
            return cached;
        }

        Texture sheet = new Texture(Gdx.files.internal(texturePath));
        sheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        TextureRegion[][] split = TextureRegion.split(sheet, FRAME_SIZE, FRAME_SIZE);
        TextureRegion[] frames = split[0];
        Animation<TextureRegion> animation = new Animation<>(FRAME_DURATION, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        CACHE.put(texturePath, animation);
        return animation;
    }
}
