package io.github.simple_game.core.service;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.model.entity.TowerType;

/**
 * Сервис обработки пользовательского ввода и сложных жестов взаимодействия с игровым миром.
 * Поддерживает жесты Pinch-to-Zoom, Pan (перетаскивание) и Drag-and-Drop башен из магазина.
 */
public class InteractionService implements GestureDetector.GestureListener {
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;

    private TowerType selectedTowerType = TowerType.ARCHER;
    private final Vector3 touchPoint = new Vector3();

    private static final int CELL_SIZE = 32;

    private float initialZoom = 1.0f;
    private final float minZoom = 0.5f;
    private final float maxZoom = 2.0f;

    private final Vector2 velocity = new Vector2();
    private final float friction = 0.90f;
    private static final float PAN_SENSITIVITY = 0.6f;

    private final DragAndDropManager dragAndDropManager;
    private static final float SHOP_HEIGHT = 100f; // Высота магазина внизу экрана

    /**
     * Создает новый сервис взаимодействия и жестов.
     *
     * @param gameLoop актуальная ссылка на игровой цикл
     * @param camera   ортографическая камера игрового мира
     */
    public InteractionService(GameLoop gameLoop, OrthographicCamera camera) {
        this.gameLoop = gameLoop;
        this.camera = camera;
        this.dragAndDropManager = new DragAndDropManager(gameLoop, camera);
    }

    /**
     * Ловим момент первого прикосновения к экрану.
     * Проверяет, попал ли палец на плашку магазина для старта перетаскивания.
     */
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        touchPoint.set(x, y, 0);
        camera.unproject(touchPoint);

        if (touchPoint.y <= SHOP_HEIGHT) {
            TowerType typeToDrag = TowerType.ARCHER;
            if (touchPoint.x > 160 && touchPoint.x <= 320) typeToDrag = TowerType.CANNON;
            if (touchPoint.x > 320) typeToDrag = TowerType.MAGIC;

            dragAndDropManager.startDrag(typeToDrag, x, y);
            return true;
        }

        velocity.set(0, 0);
        return false;
    }

    /**
     * Срабатывает при одиночном коротком тапе по экрану.
     * Используется для улучшения существующих башен.
     */
    @Override
    public boolean tap(float x, float y, int count, int button) {
        touchPoint.set(x, y, 0);
        camera.unproject(touchPoint);

        float snappedX = ((int) touchPoint.x / CELL_SIZE) * CELL_SIZE + (CELL_SIZE / 2f);
        float snappedY = ((int) touchPoint.y / CELL_SIZE) * CELL_SIZE + (CELL_SIZE / 2f);

        CurrencyManager economy = gameLoop.getCurrencyManager();
        for (Tower tower : gameLoop.getTowers()) {
            if (tower.getPosition().dst(snappedX, snappedY) < CELL_SIZE) {
                int upgradeCost = tower.getUpgradeCost();
                if (economy.spendGold(upgradeCost)) {
                    tower.tryUpgrade();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Срабатывает при движении пальца.
     * Либо тащит башенку из магазина, либо перемещает саму карту.
     */
    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (dragAndDropManager.isDragging()) {
            dragAndDropManager.updatePosition(x, y);
        } else {
            velocity.set(0, 0);
            camera.position.add(-deltaX * camera.zoom * PAN_SENSITIVITY, deltaY * camera.zoom * PAN_SENSITIVITY, 0);
            clampCamera();
        }
        return true;
    }

    /**
     * Срабатывает, когда палец отрывается от экрана после перемещения.
     */
    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        if (dragAndDropManager.isDragging()) {
            dragAndDropManager.stopDragAndPlace();
            return true;
        }
        return false;
    }

    @Override
    public boolean fling(float vx, float vy, int button) {
        if (dragAndDropManager.isDragging()) return false;
        velocity.set(-vx * camera.zoom * PAN_SENSITIVITY, vy * camera.zoom * PAN_SENSITIVITY);
        return true;
    }

    public void updateInertia(float deltaTime) {
        if (velocity.len() < 10f) {
            velocity.set(0, 0);
            return;
        }
        camera.position.add(velocity.x * deltaTime, velocity.y * deltaTime, 0);
        velocity.scl((float) Math.pow(friction, deltaTime * 60f));
        clampCamera();
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        if (dragAndDropManager.isDragging()) return false;
        float initialDistance = initialPointer1.dst(initialPointer2);
        float currentDistance = pointer1.dst(pointer2);

        if (initialDistance == 0) return false;

        float ratio = initialDistance / currentDistance;
        camera.zoom = Math.max(minZoom, Math.min(maxZoom, initialZoom * ratio));
        clampCamera();
        return true;
    }

    @Override
    public void pinchStop() {
        initialZoom = camera.zoom;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance == 0) return false;
        float ratio = initialDistance / distance;
        camera.zoom = Math.max(minZoom, Math.min(maxZoom, initialZoom * ratio));
        clampCamera();
        return true;
    }

    private void clampCamera() {
        float worldWidth = 480f;
        float worldHeight = 800f;

        float halfViewportWidth = (camera.viewportWidth * camera.zoom) / 2f;
        float halfViewportHeight = (camera.viewportHeight * camera.zoom) / 2f;

        if (halfViewportWidth * 2f > worldWidth) {
            camera.zoom = worldWidth / camera.viewportWidth;
            halfViewportWidth = worldWidth / 2f;
        }
        if (halfViewportHeight * 2f > worldHeight) {
            camera.zoom = worldHeight / camera.viewportHeight;
            halfViewportHeight = worldHeight / 2f;
        }

        camera.position.x = Math.max(halfViewportWidth, Math.min(worldWidth - halfViewportWidth, camera.position.x));
        camera.position.y = Math.max(halfViewportHeight, Math.min(worldHeight - halfViewportHeight, camera.position.y));
    }

    public DragAndDropManager getDragAndDropManager() { return dragAndDropManager; }
    public void setSelectedTowerType(TowerType selectedTowerType) { this.selectedTowerType = selectedTowerType; }
    @Override public boolean longPress(float x, float y) { return false; }
}
