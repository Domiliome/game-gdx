package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectMap;

import io.github.simple_game.core.model.entity.items.Item;

/**
 * Кэш иконок предметов. PNG лежат в {@code assets/items/} по пути {@link Item#getIconPath()}.
 */
public final class ItemIcons {
    private static final ObjectMap<String, Texture> CACHE = new ObjectMap<>();

    private ItemIcons() {}

    public static Texture get(Item item) {
        if (item == null) return null;
        String path = item.getIconPath();
        if (!CACHE.containsKey(path)) {
            if (Gdx.files.internal(path).exists()) {
                Texture texture = new Texture(Gdx.files.internal(path));
                texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                CACHE.put(path, texture);
            } else {
                CACHE.put(path, null);
            }
        }
        return CACHE.get(path);
    }

    public static Image createImage(Item item, float size) {
        Texture texture = get(item);
        if (texture == null) return null;
        Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        image.setSize(size, size);
        return image;
    }
}
