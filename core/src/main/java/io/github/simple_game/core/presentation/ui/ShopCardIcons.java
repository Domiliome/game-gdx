package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

import io.github.simple_game.core.model.entity.tower.TowerType;

/**
 * Кэш PNG-карточек магазина из {@code assets/card/}.
 */
public final class ShopCardIcons {
    private static final ObjectMap<TowerType, Texture> CACHE = new ObjectMap<>();

    private ShopCardIcons() {}

    public static Texture get(TowerType type) {
        if (type == null) return null;
        if (CACHE.containsKey(type)) return CACHE.get(type);

        String path = type.getCardTexturePath();
        if (Gdx.files.internal(path).exists()) {
            Texture texture = new Texture(Gdx.files.internal(path));
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            CACHE.put(type, texture);
            return texture;
        }

        CACHE.put(type, null);
        return null;
    }
}
