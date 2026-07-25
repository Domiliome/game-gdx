package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.Projectile;
import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InteractionService;

/**
 * Класс подсистемы отрисовки графики игрового мира и интерфейса магазина.
 */
public class GameRenderer {
    private final BitmapFont shopFont;
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;
    private final InteractionService interactionService;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    private final Texture mapTexture;
    private final Texture archerTowerTexture;

    private static final int CELL_SIZE = 32;
    private static final int WORLD_WIDTH = 480;
    private static final int WORLD_HEIGHT = 800;

    /**
     * Создает новый рендерер игрового мира и загружает пиксельные ассеты.
     *
     * @param gameLoop           актуальная ссылка на игровой цикл
     * @param camera             ортографическая камера экрана
     * @param interactionService ссылка на сервис взаимодействия для Drag and Drop превью
     */
    public GameRenderer(GameLoop gameLoop, OrthographicCamera camera, InteractionService interactionService) {
        this.gameLoop = gameLoop;
        this.camera = camera;
        this.interactionService = interactionService;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.mapTexture = new Texture(Gdx.files.internal("map.png"));
        this.archerTowerTexture = new Texture(Gdx.files.internal("tower_archer.png"));

        this.mapTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.archerTowerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.shopFont = new BitmapFont();
        this.shopFont.setColor(Color.GOLD); // Ценники будут золотого цвета
        this.shopFont.getData().setScale(1.2f); // Чуть меньше, чем основной интерфейс волн
        this.shopFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void render() {
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        renderSprites();
        renderShapes();
        renderShopUI();
    }

    private void renderSprites() {
        batch.begin();
        batch.draw(mapTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        for (Tower tower : gameLoop.getTowers()) {
            float drawX = tower.getPosition().x - 16f;
            float drawY = tower.getPosition().y - 16f;
            batch.draw(archerTowerTexture, drawX, drawY, 32, 32);
        }
        batch.end();
    }

    private void renderShapes() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.1f));
        for (int x = 0; x <= WORLD_WIDTH; x += CELL_SIZE) {
            shapeRenderer.line(x, 0, x, WORLD_HEIGHT);
        }
        for (int y = 0; y <= WORLD_HEIGHT; y += CELL_SIZE) {
            shapeRenderer.line(0, y, WORLD_WIDTH, y);
        }
        shapeRenderer.end();

        RoadPath path = (RoadPath) gameLoop.getRoadPath();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);
        for (int i = 0; i < path.getPointCount() - 1; i++) {
            shapeRenderer.line(path.getPoint(i), path.getPoint(i + 1));
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.2f));
        for (Tower tower : gameLoop.getTowers()) {
            shapeRenderer.circle(tower.getPosition().x, tower.getPosition().y, tower.getAttackRange());
        }
        shapeRenderer.end();

        // Безопасно отрисовываем превью сетки через прямую ссылку
        if (interactionService != null) {
            interactionService.getDragAndDropManager().drawPreview(shapeRenderer);
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.PURPLE);
        for (Enemy enemy : gameLoop.getEnemies()) {
            shapeRenderer.circle(enemy.getPosition().x, enemy.getPosition().y, 10);
        }

        shapeRenderer.setColor(Color.YELLOW);
        for (Projectile projectile : gameLoop.getProjectiles()) {
            shapeRenderer.circle(projectile.getPosition().x, projectile.getPosition().y, 3);
        }

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
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Отрисовывает статичную графическую плашку магазина внизу экрана,
     * разделяет её на кнопки, выводит иконки товаров и текстовые ценники под ними.
     */
    private void renderShopUI() {
        if (interactionService == null) return;

        // 1. Отрисовка полупрозрачного фантома башни, летящего за пальцем
        if (interactionService.getDragAndDropManager().isDragging()) {
            batch.begin();
            batch.setColor(1, 1, 1, 0.6f);
            batch.draw(archerTowerTexture, interactionService.getDragAndDropManager().getCurrentX() - 16, interactionService.getDragAndDropManager().getCurrentY() - 16, 32, 32);
            batch.setColor(1, 1, 1, 1f);
            batch.end();
        }

        // 2. Отрисовка нижней плашки магазина
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(0, 0, WORLD_WIDTH, 100f);

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(158, 10, 4, 80);
        shapeRenderer.rect(318, 10, 4, 80);
        shapeRenderer.end();

        // 3. Отрисовка иконок башен внутри слотов магазина
        batch.begin();
        batch.draw(archerTowerTexture, 64, 44, 32, 32);  // Подняли иконку чуть выше (y=44), чтобы освободить место под текст
        batch.draw(archerTowerTexture, 224, 44, 32, 32);
        batch.draw(archerTowerTexture, 384, 44, 32, 32);

        // 4. Отрисовка ценников золотым цветом под каждой иконкой
        String archerCost = io.github.simple_game.core.model.entity.TowerType.ARCHER.getCost() + "G";
        String cannonCost = io.github.simple_game.core.model.entity.TowerType.CANNON.getCost() + "G";
        String magicCost = io.github.simple_game.core.model.entity.TowerType.MAGIC.getCost() + "G";

        // Позиционируем текст по центру каждого из трех слотов под иконками
        shopFont.draw(batch, archerCost, 60, 25);
        shopFont.draw(batch, cannonCost, 216, 25);
        shopFont.draw(batch, magicCost, 376, 25);

        batch.end();
    }


    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        mapTexture.dispose();
        archerTowerTexture.dispose();
        shopFont.dispose();

    }
}
