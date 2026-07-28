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
    private final Texture archerTexture, cannonTexture, magicTexture;
    private static final int CELL_SIZE = 64;

    public WorldSpriteRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        this.mapTexture = new Texture(Gdx.files.internal("map.png"));
        this.archerTexture = new Texture(Gdx.files.internal("tower_archer.png"));
        this.cannonTexture = new Texture(Gdx.files.internal("tower_cannon.png"));
        this.magicTexture = new Texture(Gdx.files.internal("tower_magic.png"));
        mapTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void render(SpriteBatch batch, float worldHeight) {
        batch.draw(mapTexture, 0, 0, 480, worldHeight);
        for (Tower tower : gameLoop.getTowers()) {
            // Смещение центра теперь 16 пикселей (CELL_SIZE / 2), чтобы башня стояла ровно в узле сетки
            float x = tower.getPosition().x - (CELL_SIZE / 2f);
            float y = tower.getPosition().y - (CELL_SIZE / 2f);
            batch.draw(getTexture(tower.getType()), x, y, CELL_SIZE, CELL_SIZE);
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
