package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;

public class EntityRenderer {
    private final GameLoop gameLoop;

    public EntityRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void render(ShapeRenderer shapeRenderer) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 1. Отрисовка цветных врагов
        for (Enemy enemy : gameLoop.getEnemies()) {
            if (!enemy.isActive()) continue;

            switch (enemy.getTier()) {
                case TIER_1_LIGHT  -> shapeRenderer.setColor(Color.YELLOW);
                case TIER_2_NORMAL -> shapeRenderer.setColor(Color.GREEN);
                case TIER_3_HEAVY  -> shapeRenderer.setColor(Color.BLUE);
                default            -> shapeRenderer.setColor(Color.PURPLE);
            }
            shapeRenderer.circle(enemy.getPosition().x, enemy.getPosition().y, 12f);
        }


        shapeRenderer.setColor(Color.GOLD);
        for (Projectile projectile : gameLoop.getProjectiles()) {
            if (projectile.isActive()) {

                shapeRenderer.circle(projectile.getPosition().x, projectile.getPosition().y, 5f);
            }
        }

        // ЗАКРЫВАЕМ СЕССИЮ
        shapeRenderer.end();
    }
}
