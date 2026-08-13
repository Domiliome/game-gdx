package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.enemy.FastGoblin;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;

public class EntityRenderer {
    private final GameLoop gameLoop;
    private final SpriteBatch spriteBatch;
    private static final int VISUAL_SIZE = 64; // Наш масштаб 2х

    public EntityRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        this.spriteBatch = new SpriteBatch();
    }

    public void render(ShapeRenderer shapeRenderer) {
        spriteBatch.setProjectionMatrix(shapeRenderer.getProjectionMatrix());

        // --- СЛОЙ 1: ОТРИСОВКА ПИКСЕЛЬНЫХ ТЕКСТУР ГОБЛИНОВ ---
        spriteBatch.begin();
        for (Enemy enemy : gameLoop.getEnemies()) {
            if (!enemy.isActive()) continue;

            if (enemy instanceof FastGoblin goblin) {
                TextureRegion currentFrame = goblin.getCurrentGoblinFrame();
                if (currentFrame != null) {
                    float drawX = goblin.getPosition().x - (VISUAL_SIZE / 2f);
                    float drawY = goblin.getPosition().y - (VISUAL_SIZE / 2f);

                    // Извлекаем рассчитанный угол в градусах (0, 90, 180, 270)
                    float rotation = goblin.getCurrentRotation();

                    // ИСПРАВЛЕНО: Расширенный метод SpriteBatch с поддержкой вращения видеочипом.
                    // Параметры 32f, 32f задают центр вращения точно посередине гоблина,
                    // обеспечивая безупречный и плавный разворот во все 4 стороны лабиринта!
                    spriteBatch.draw(
                        currentFrame,
                        drawX, drawY,
                        32f, 32f,                 // Точка опоры (Origin) X и Y
                        VISUAL_SIZE, VISUAL_SIZE, // Размеры отрисовки
                        1f, 1f,                   // Масштаб X и Y
                        rotation                  // Угол вращения в градусах
                    );
                }
            }
        }
        spriteBatch.end();

        // --- СЛОЙ 2: ОТРИСОВКА ГЕОМЕТРИИ (Остальные мобы и золотые пули) ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Enemy enemy : gameLoop.getEnemies()) {
            if (!enemy.isActive()) continue;

            if (!(enemy instanceof FastGoblin)) {
                switch (enemy.getTier()) {
                    case TIER_1_LIGHT  -> shapeRenderer.setColor(Color.YELLOW);
                    case TIER_2_NORMAL -> shapeRenderer.setColor(Color.GREEN);
                    case TIER_3_HEAVY  -> shapeRenderer.setColor(Color.BLUE);
                    default            -> shapeRenderer.setColor(Color.PURPLE);
                }
                shapeRenderer.circle(enemy.getPosition().x, enemy.getPosition().y, 12f);
            }
        }

        shapeRenderer.setColor(Color.GOLD);
        for (Projectile projectile : gameLoop.getProjectiles()) {
            if (projectile.isActive()) {
                shapeRenderer.circle(projectile.getPosition().x, projectile.getPosition().y, 5f);
            }
        }

        shapeRenderer.end();
    }
}
