package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.github.simple_game.core.model.entity.tower.TowerType;

/** Спрайты появления башен. Текстуры живут в презентации, не в модели. */
public final class TowerSprites {
    private static final int FRAME_SIZE = 32;
    private static final float FRAME_DURATION = 0.06f;

    private final ObjectMap<TowerType, Animation<TextureRegion>> initAnims = new ObjectMap<>();
    private final Array<Texture> textures = new Array<>();

    public TowerSprites() {
        for (TowerType type : TowerType.values()) {
            Texture sheet = new Texture(Gdx.files.internal(type.getInitTexturePath()));
            sheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            TextureRegion[][] tmp = TextureRegion.split(sheet, FRAME_SIZE, FRAME_SIZE);
            int totalFrames = tmp[0].length;
            TextureRegion[] animationFrames = new TextureRegion[totalFrames];
            System.arraycopy(tmp[0], 0, animationFrames, 0, totalFrames);
            Animation<TextureRegion> animation = new Animation<>(FRAME_DURATION, animationFrames);
            initAnims.put(type, animation);
            textures.add(sheet);
        }
    }

    public TextureRegion getInitFrame(TowerType type, float animationTime) {
        Animation<TextureRegion> animation = initAnims.get(type);
        return animation != null ? animation.getKeyFrame(animationTime) : null;
    }

    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
        textures.clear();
        initAnims.clear();
    }
}
