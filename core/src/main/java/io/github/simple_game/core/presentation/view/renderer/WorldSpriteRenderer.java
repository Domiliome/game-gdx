package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.simple_game.core.model.entity.tower.ArcherTower;
import io.github.simple_game.core.model.entity.tower.CannonTower;
import io.github.simple_game.core.model.entity.tower.MagicTower;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.entity.tower.TowerType;
import io.github.simple_game.core.service.GameLoop;

public class WorldSpriteRenderer {
    private final GameLoop gameLoop;
    private final Texture mapTexture;
    private final Texture archerTexture, cannonTexture, magicTexture;
    private static final int CELL_SIZE = 64; // Фиксированный крупный визуальный масштаб

    public WorldSpriteRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        this.mapTexture = new Texture(Gdx.files.internal("map.png"));
        this.archerTexture = new Texture(Gdx.files.internal("tower_archer.png"));
        this.cannonTexture = new Texture(Gdx.files.internal("tower_cannon.png"));
        this.magicTexture = new Texture(Gdx.files.internal("tower_magic.png"));

        mapTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        archerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        cannonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        magicTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    /**
     * ШАГ 1: Отрисовывает исключительно коричневый фон земли и моря.
     * Вызывается в самом начале кадра.
     */
    public void renderBackground(SpriteBatch batch, float worldHeight) {
        batch.draw(mapTexture, 0, 0, 480, worldHeight);
    }


    public void renderTowers(SpriteBatch batch) {
        for (Tower tower : gameLoop.getTowers()) {
            float drawX = tower.getPosition().x - (CELL_SIZE / 2f);
            float drawY = tower.getPosition().y - (CELL_SIZE / 2f);

            // 1. АНИМАЦИЯ ЛУЧНИКА
            if (tower.getType() == TowerType.ARCHER && tower instanceof ArcherTower) {
                ArcherTower archer = (ArcherTower) tower;
                if (archer.isInitializing()) {
                    TextureRegion currentFrame = archer.getCurrentInitFrame();
                    if (currentFrame != null) {
                        batch.draw(currentFrame, drawX, drawY, CELL_SIZE, CELL_SIZE);
                        continue;
                    }
                }
            }

            // 2. АНИМАЦИЯ МАГА
            if (tower.getType() == TowerType.MAGIC && tower instanceof MagicTower) {
                MagicTower magic = (MagicTower) tower;
                if (magic.isInitializing()) {
                    TextureRegion currentFrame = magic.getCurrentInitFrame();
                    if (currentFrame != null) {
                        batch.draw(currentFrame, drawX, drawY, CELL_SIZE, CELL_SIZE);
                        continue;
                    }
                }
            }

            // --- ДОБАВЬТЕ ЭТОТ ФИНАЛЬНЫЙ БЛОК ДЛЯ ПУШКИ СЮДА ---
            // 3. АНИМАЦИЯ ПУШКИ
            if (tower.getType() == TowerType.CANNON && tower instanceof CannonTower) {
                CannonTower cannon = (CannonTower) tower;
                if (cannon.isInitializing()) {
                    TextureRegion currentFrame = cannon.getCurrentInitFrame();
                    if (currentFrame != null) {
                        batch.draw(currentFrame, drawX, drawY, CELL_SIZE, CELL_SIZE);
                        continue; // Пропускаем статичный спрайт готовой пушки
                    }
                }
            }
            // ---------------------------------------------------

            // Статичная отрисовка любой готовой башни (размер 64х64)
            batch.draw(getTexture(tower.getType()), drawX, drawY, CELL_SIZE, CELL_SIZE);
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
        mapTexture.dispose(); archerTexture.dispose();
        cannonTexture.dispose(); magicTexture.dispose();
    }
}
