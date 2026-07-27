package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.entity.tower.TowerType;
import io.github.simple_game.core.service.GameLoop;

public class WorldSpriteRenderer {
    private final GameLoop gameLoop;
    private final Texture mapTexture;
    private final Texture archerTexture;
    private final Texture cannonTexture;
    private final Texture magicTexture;

    public WorldSpriteRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        this.mapTexture = new Texture(Gdx.files.internal("map.png"));
        this.archerTexture = new Texture(Gdx.files.internal("tower_archer.png"));
        this.cannonTexture = new Texture(Gdx.files.internal("tower_cannon.png"));
        this.magicTexture = new Texture(Gdx.files.internal("tower_magic.png"));

        mapTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void render(SpriteBatch batch) {
        batch.draw(mapTexture, 0, 0, 480, 800);
        for (Tower tower : gameLoop.getTowers()) {
            float x = tower.getPosition().x - 32f;
            float y = tower.getPosition().y - 32f;
            batch.draw(getTexture(tower.getType()), x, y, 64, 64);
        }
    }

    public Texture getTexture(TowerType type) {
        return switch (type) {
            case ARCHER -> archerTexture;
            case CANNON -> cannonTexture;
            case MAGIC  -> magicTexture;
        };
    }

    public void dispose() {
        mapTexture.dispose();
        archerTexture.dispose();
        cannonTexture.dispose();
        magicTexture.dispose();
    }
}
