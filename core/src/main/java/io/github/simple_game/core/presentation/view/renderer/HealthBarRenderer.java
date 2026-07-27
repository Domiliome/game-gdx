package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.service.GameLoop;

public class HealthBarRenderer {
    private final GameLoop gameLoop;

    public HealthBarRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : gameLoop.getEnemies()) {
            float x = enemy.getPosition().x;
            float y = enemy.getPosition().y;
            float width = 30f;
            float height = 4f;

            // Красная подложка (урон)
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(x - width / 2, y + 20f, width, height);

            // Зеленая шкала (текущее HP)
            float hpPercent = Math.max(0f, enemy.getHealth() / 100f);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(x - width / 2, y + 20f, width * hpPercent, height);
        }
        shapeRenderer.end();
    }
}
