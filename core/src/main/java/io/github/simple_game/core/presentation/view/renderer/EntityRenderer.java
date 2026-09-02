package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.map.GameGrid;
import io.github.simple_game.core.model.entity.projectile.PoisonBolt;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.model.entity.tower.TeslaTower;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.service.GameLoop;

public class EntityRenderer {
    private final GameLoop gameLoop;
    private final SpriteBatch spriteBatch;
    private static final int BASE_VISUAL_SIZE = GameGrid.CELL_SIZE + 16;

    public EntityRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        this.spriteBatch = new SpriteBatch();
    }

    public void render(ShapeRenderer shapeRenderer) {
        spriteBatch.setProjectionMatrix(shapeRenderer.getProjectionMatrix());

        spriteBatch.begin();
        for (Enemy enemy : gameLoop.getEnemies()) {
            if (!enemy.isActive()) continue;

            TextureRegion frame = EnemySprites.runAnimation(enemy.getSpritePath())
                    .getKeyFrame(enemy.getAnimationTime());
            if (frame == null) continue;

            float size = BASE_VISUAL_SIZE * enemy.getVisualScale();
            float drawX = enemy.getPosition().x - (size / 2f);
            float drawY = enemy.getPosition().y - (size / 2f);
            float origin = size / 2f;

            spriteBatch.draw(
                    frame,
                    drawX, drawY,
                    origin, origin,
                    size, size,
                    1f, 1f,
                    enemy.getCurrentRotation()
            );
        }
        spriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.GOLD);
        for (Projectile projectile : gameLoop.getProjectiles()) {
            if (projectile.isActive()) {
                if (projectile instanceof PoisonBolt) {
                    shapeRenderer.setColor(Color.LIME);
                } else {
                    shapeRenderer.setColor(Color.GOLD);
                }
                shapeRenderer.circle(projectile.getPosition().x, projectile.getPosition().y, 5f);
            }
        }

        shapeRenderer.end();

        renderTeslaBeams(shapeRenderer);
    }

    private void renderTeslaBeams(ShapeRenderer shapeRenderer) {
        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        com.badlogic.gdx.Gdx.gl.glBlendFunc(
                com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Tower tower : gameLoop.getTowers()) {
            if (!(tower instanceof TeslaTower tesla) || !tesla.shouldDrawBeam()) {
                continue;
            }
            TeslaTower partner = tesla.getPartner();
            float x1 = tesla.getPosition().x;
            float y1 = tesla.getPosition().y;
            float x2 = partner.getPosition().x;
            float y2 = partner.getPosition().y;

            shapeRenderer.setColor(0.15f, 0.55f, 1f, 0.4f);
            shapeRenderer.rectLine(x1, y1, x2, y2, 8f);
            shapeRenderer.setColor(0.55f, 0.9f, 1f, 0.95f);
            shapeRenderer.rectLine(x1, y1, x2, y2, 3f);
            shapeRenderer.setColor(1f, 1f, 1f, 1f);
            shapeRenderer.rectLine(x1, y1, x2, y2, 1.2f);
        }
        shapeRenderer.end();
    }
}
