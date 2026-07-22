package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.Projectile;
import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.service.GameLoop;

/**
 * Класс подсистемы отрисовки графики игрового мира.
 * Визуализирует пиксельные текстуры карты, башен, а также временные заглушки для врагов и снарядов.
 */
public class GameRenderer {
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    // Ссылки на текстуры
    private final Texture mapTexture;
    private final Texture archerTowerTexture;

    /**
     * Создает новый рендерер игрового мира и загружает пиксельные ассеты.
     *
     * @param gameLoop актуальная ссылка на игровой цикл
     * @param camera   ортографическая камера экрана
     */
    public GameRenderer(GameLoop gameLoop, OrthographicCamera camera) {
        this.gameLoop = gameLoop;
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        // Загружаем текстуры из папки assets
        this.mapTexture = new Texture(Gdx.files.internal("map.png"));
        this.archerTowerTexture = new Texture(Gdx.files.internal("tower_archer.png"));

        // Включаем "ближайшую" фильтрацию, чтобы пиксель-арт оставался четким и не размывался
        this.mapTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.archerTowerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    /**
     * Главный метод рендеринга кадра.
     */
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // 1. Сначала рисуем растровые текстуры фона и башен
        renderSprites();

        // 2. Поверх текстур рисуем геометрический дебаг (радиусы, полоски здоровья, врагов)
        renderShapes();
    }


    /**
     * Отрисовка графических спрайтов и фонов.
     */
    private void renderSprites() {
        batch.begin();

        // Рисуем карту на весь экран (от левого нижнего угла 0,0 до размеров 480x800)
        batch.draw(mapTexture, 0, 0, 480, 800);

        // Рисуем башни из игрового цикла
        for (Tower tower : gameLoop.getTowers()) {
            // Координаты башни в коде — это её центр.
            // Чтобы спрайт 32x32 встал ровно по центру, смещаем отрисовку на половину размера ячейки (16 пикселей)
            float drawX = tower.getPosition().x - 16f;
            float drawY = tower.getPosition().y - 16f;

            batch.draw(archerTowerTexture, drawX, drawY, 32, 32);
        }

        batch.end();
    }

    /**
     * Визуализирует геометрические элементы и заглушки.
     */
    private void renderShapes() {
        // Отрисовка радиусов атаки башен
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.2f)); // Чуть более тусклый белый
        for (Tower tower : gameLoop.getTowers()) {
            shapeRenderer.circle(tower.getPosition().x, tower.getPosition().y, tower.getAttackRange());
        }
        shapeRenderer.end();

        // Отрисовка врагов, снарядов и полосок здоровья
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Враги (фиолетовые круги)
        shapeRenderer.setColor(Color.PURPLE);
        for (Enemy enemy : gameLoop.getEnemies()) {
            shapeRenderer.circle(enemy.getPosition().x, enemy.getPosition().y, 10);
        }

        // Снаряды (желтые круги)
        shapeRenderer.setColor(Color.YELLOW);
        for (Projectile projectile : gameLoop.getProjectiles()) {
            shapeRenderer.circle(projectile.getPosition().x, projectile.getPosition().y, 3);
        }


        // Полоски здоровья над врагами
        for (Enemy enemy : gameLoop.getEnemies()) {
            float enemyX = enemy.getPosition().x;
            float enemyY = enemy.getPosition().y;
            float barWidth = 30f;
            float barHeight = 4f;
            float barYOffset = 20f;

            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(enemyX - barWidth / 2, enemyY + barYOffset, barWidth, barHeight);

            float healthPercentage = Math.max(0f, enemy.getHealth() / 100f);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(enemyX - barWidth / 2, enemyY + barYOffset, barWidth * healthPercentage, barHeight);
        }
        shapeRenderer.end();
    }

    /**
     * Освобождает текстуры и инструменты из видеопамяти.
     */
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        mapTexture.dispose();
        archerTowerTexture.dispose();
    }
}
