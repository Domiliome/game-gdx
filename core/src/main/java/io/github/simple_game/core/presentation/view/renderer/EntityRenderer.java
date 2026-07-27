package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.projectile.Arrow;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;

public class EntityRenderer {
    private final GameLoop gameLoop;

    public EntityRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 1. Враги
        shapeRenderer.setColor(Color.PURPLE);
        for (Enemy enemy : gameLoop.getEnemies()) {
            shapeRenderer.circle(enemy.getPosition().x, enemy.getPosition().y, 10);
        }

        // 2. Снаряды
        for (Projectile proj : gameLoop.getProjectiles()) {
            boolean isCrit = (proj instanceof Arrow arrow && arrow.isCritical());
            shapeRenderer.setColor(isCrit ? Color.ORANGE : Color.YELLOW);
            shapeRenderer.circle(proj.getPosition().x, proj.getPosition().y, isCrit ? 5 : 3);
        }
        shapeRenderer.end();
    }
}
