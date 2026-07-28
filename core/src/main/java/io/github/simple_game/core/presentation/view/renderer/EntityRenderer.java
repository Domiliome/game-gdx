package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;

/**
 * Модульный отрисовщик физических объектов игрового мира.
 * Визуализирует фигурки врагов и летящие снаряды с помощью геометрических примитивов.
 */
public class EntityRenderer {
    private final GameLoop gameLoop;

    public EntityRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void render(ShapeRenderer shapeRenderer) {
        // 1. Отрисовка врагов в виде закрашенных цветных кружков
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : gameLoop.getEnemies()) {
            if (!enemy.isActive()) continue;

            // Динамически подкрашиваем фигурку юнита в зависимости от его класса (тира)
            switch (enemy.getTier()) {
                case TIER_1_LIGHT  -> shapeRenderer.setColor(Color.YELLOW); // Гоблины — желтые
                case TIER_2_NORMAL -> shapeRenderer.setColor(Color.GREEN);  // Зอมби — зеленые
                case TIER_3_HEAVY  -> shapeRenderer.setColor(Color.BLUE);   // Орки — синие
                default            -> shapeRenderer.setColor(Color.PURPLE); // Защитный дефолтный цвет
            }

            // Рисуем кружок врага радиусом 12 пикселей (под мелкую сетку 32х32)
            shapeRenderer.circle(enemy.getPosition().x, enemy.getPosition().y, 12f);
        }

        // 2. Отрисовка летящих снарядов башен (маленькие желтые точки)
        shapeRenderer.setColor(Color.GOLD);
        for (Projectile projectile : gameLoop.getProjectiles()) {
            if (projectile.isActive()) {
                shapeRenderer.circle(projectile.getPosition().x, projectile.getPosition().y, 4f);
            }
        }
        shapeRenderer.end();
    }
}
