package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.service.GameLoop;

public class HealthBarRenderer {
    private final GameLoop gameLoop;
    private final BitmapFont lvlFont;

    public HealthBarRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        this.lvlFont = new BitmapFont();
        this.lvlFont.setColor(Color.CYAN); // Бирюзовый цвет для уровней башен
        this.lvlFont.getData().setScale(1.2f); // Компактный размер текста
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // 1. Рисуем полоски здоровья врагов (ShapeRenderer Filled)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : gameLoop.getEnemies()) {
            float x = enemy.getPosition().x;
            float y = enemy.getPosition().y;
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(x - 15, y + 20f, 30, 4f);
            float hpPercent = Math.max(0f, enemy.getHealth() / 100f);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(x - 15, y + 20f, 30 * hpPercent, 4f);
        }
        shapeRenderer.end();

        // 2. Рисуем уровень над головой каждой башни (SpriteBatch)
        batch.begin();
        for (Tower tower : gameLoop.getTowers()) {
            float x = tower.getPosition().x;
            float y = tower.getPosition().y;
            String lvlText = "Lvl " + tower.getCurrentLevel();

            lvlFont.draw(batch, lvlText, x - 18f, y + 35f);
        }
        batch.end();
    }
}
