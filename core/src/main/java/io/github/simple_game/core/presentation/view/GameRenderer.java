package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.projectile.Arrow;
import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.model.entity.map.ShopSlot;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.entity.tower.TowerType;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InteractionService;

/**
 * Класс подсистемы отрисовки графики игрового мира и интерфейса магазина.
 * Визуализирует пиксельные текстуры карты, уникальные спрайты башен, а также геометрическую сетку,
 * радиусы атаки, врагов, обычные и критические снаряды, шкалу здоровья и Drag-and-Drop магазин.
 */
public class GameRenderer {
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;
    private final InteractionService interactionService;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont shopFont;

    private final Texture mapTexture;
    private final Texture archerTowerTexture;
    private final Texture cannonTowerTexture;
    private final Texture magicTowerTexture;

    private static final int CELL_SIZE = 64;
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

        this.shopFont = new BitmapFont();
        this.shopFont.setColor(Color.GOLD);
        this.shopFont.getData().setScale(1.2f);
        this.shopFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        this.mapTexture = new Texture(Gdx.files.internal("map.png"));
        this.mapTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        this.archerTowerTexture = new Texture(Gdx.files.internal("tower_archer.png"));
        this.cannonTowerTexture = new Texture(Gdx.files.internal("tower_cannon.png"));
        this.magicTowerTexture = new Texture(Gdx.files.internal("tower_magic.png"));

        this.archerTowerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.cannonTowerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        this.magicTowerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    /**
     * Вспомогательный метод, сопоставляющий логический тип башни с её физической текстурой.
     */
    private Texture getTextureForType(TowerType type) {
        return switch (type) {
            case ARCHER -> archerTowerTexture;
            case CANNON -> cannonTowerTexture;
            case MAGIC  -> magicTowerTexture;
        };
    }

    /**
     * Главный метод рендеринга кадра.
     */
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
            float drawX = tower.getPosition().x - 32f;
            float drawY = tower.getPosition().y - 32f;
            batch.draw(getTextureForType(tower.getType()), drawX, drawY, CELL_SIZE, CELL_SIZE);
        }
        batch.end();
    }

    private void renderShapes() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // 1. Отрисовка координатной сетки
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.1f));
        for (int x = 0; x <= WORLD_WIDTH; x += CELL_SIZE) {
            shapeRenderer.line(x, 0, x, WORLD_HEIGHT);
        }
        for (int y = 0; y <= WORLD_HEIGHT; y += CELL_SIZE) {
            shapeRenderer.line(0, y, WORLD_WIDTH, y);
        }
        shapeRenderer.end();

        // 2. Отрисовка пути врагов
        RoadPath path = (RoadPath) gameLoop.getRoadPath();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);
        for (int i = 0; i < path.getPointCount() - 1; i++) {
            shapeRenderer.line(path.getPoint(i), path.getPoint(i + 1));
        }
        shapeRenderer.end();

        // 3. Отрисовка радиусов башен
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.2f));
        for (Tower tower : gameLoop.getTowers()) {
            shapeRenderer.circle(tower.getPosition().x, tower.getPosition().y, tower.getAttackRange());
        }
        shapeRenderer.end();

        // 4. Отрисовка превью Drag-and-Drop
        if (interactionService != null) {
            interactionService.getDragAndDropManager().drawPreview(shapeRenderer);
        }

        // 5. Отрисовка заполненных фигур (Враги, снаряды, HP)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.PURPLE);
        for (Enemy enemy : gameLoop.getEnemies()) {
            shapeRenderer.circle(enemy.getPosition().x, enemy.getPosition().y, 10);
        }

        // Рендеринг снарядов: критические стрелы выделяем крупным оранжевым цветом
        for (Projectile projectile : gameLoop.getProjectiles()) {
            if (projectile instanceof Arrow arrow && arrow.isCritical()) {
                shapeRenderer.setColor(Color.ORANGE);
                shapeRenderer.circle(projectile.getPosition().x, projectile.getPosition().y, 5);
            } else {
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.circle(projectile.getPosition().x, projectile.getPosition().y, 3);
            }
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

    private void renderShopUI() {
        if (interactionService == null) return;

        if (interactionService.getDragAndDropManager().isDragging()) {
            TowerType draggingType = interactionService.getDragAndDropManager().getDraggingType();
            if (draggingType != null) {
                batch.begin();
                batch.setColor(1, 1, 1, 0.6f);
                batch.draw(getTextureForType(draggingType),
                        interactionService.getDragAndDropManager().getCurrentX() - 32f,
                        interactionService.getDragAndDropManager().getCurrentY() - 32f,
                        CELL_SIZE, CELL_SIZE);
                batch.setColor(1, 1, 1, 1f);
                batch.end();
            }
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(0, 0, WORLD_WIDTH, 100f);
        shapeRenderer.end();

        Array<ShopSlot> slots = gameLoop.getShopService().getSlots();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (ShopSlot slot : slots) {
            shapeRenderer.rect(slot.getBounds().x, slot.getBounds().y, slot.getBounds().width, slot.getBounds().height);
        }
        shapeRenderer.end();

        batch.begin();
        for (ShopSlot slot : slots) {
            float slotX = slot.getBounds().x;
            float slotWidth = slot.getBounds().width;

            float iconX = slotX + (slotWidth / 2f) - 32f;
            float iconY = 28f;

            batch.draw(getTextureForType(slot.getTowerType()), iconX, iconY, CELL_SIZE, CELL_SIZE);

            String costText = slot.getTowerType().getCost() + "G";
            float textX = slotX + (slotWidth / 2f) - 16f;
            shopFont.draw(batch, costText, textX, 20f);
        }
        batch.end();
    }

    /**
     * Освобождает текстуры и инструменты из видеопамяти.
     */
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        shopFont.dispose();
        mapTexture.dispose();
        archerTowerTexture.dispose();
        cannonTowerTexture.dispose();
        magicTowerTexture.dispose();
    }
}
