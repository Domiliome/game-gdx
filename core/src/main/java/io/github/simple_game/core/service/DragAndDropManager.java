package io.github.simple_game.core.service;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.model.entity.TowerType;

/**
 * Менеджер, управляющий логикой перетаскивания башен из магазина на карту (Drag and Drop).
 */
public class DragAndDropManager {
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;
    private final Vector3 screenTouch = new Vector3();

    private TowerType draggingType = null;
    private boolean isDragging = false;
    private float currentX = 0f;
    private float currentY = 0f;

    private static final int CELL_SIZE = 64;

    public DragAndDropManager(GameLoop gameLoop, OrthographicCamera camera) {
        this.gameLoop = gameLoop;
        this.camera = camera;
    }

    /**
     * Вызывается, когда игрок зажал палец на иконке башни в магазине.
     */
    public void startDrag(TowerType type, float screenX, float screenY) {
        this.draggingType = type;
        this.isDragging = true;
        updatePosition(screenX, screenY);
    }

    /**
     * Вызывается каждый кадр, пока палец движется по экрану.
     */
    public void updatePosition(float screenX, float screenY) {
        if (!isDragging) return;
        screenTouch.set(screenX, screenY, 0);
        camera.unproject(screenTouch); // Переводим координаты пальца в координаты игрового мира
        this.currentX = screenTouch.x;
        this.currentY = screenTouch.y;
    }

    /**
     * Вызывается, когда игрок отпускает палец.
     */
    public void stopDragAndPlace() {
        if (!isDragging) return;
        isDragging = false;

        // Выравниваем финальную точку по сетке
        float snappedX = ((int) currentX / CELL_SIZE) * CELL_SIZE + 32f;
        float snappedY = ((int) currentY / CELL_SIZE) * CELL_SIZE + 32f;

        CurrencyManager economy = gameLoop.getCurrencyManager();

        // Проверяем, можно ли построить башню в этой клетке
        if (gameLoop.getGameGrid().isCellBuildable(snappedX, snappedY) && isCellFree(snappedX, snappedY)) {
            if (economy.spendGold(draggingType.getCost())) {
                gameLoop.addTower(new Tower(snappedX, snappedY, draggingType));
                System.out.println("Башня установлена через Drag-and-Drop!");
            }
        }
        draggingType = null;
    }

    private boolean isCellFree(float x, float y) {
        for (Tower tower : gameLoop.getTowers()) {
            if (tower.getPosition().dst(x, y) < CELL_SIZE) return false;
        }
        return true;
    }

    /**
     * Отрисовывает полупрозрачный фантом башни под пальцем и подсвечивает целевую клетку.
     */
    public void drawPreview(ShapeRenderer shapeRenderer) {
        if (!isDragging || draggingType == null) return;

        float snappedX = ((int) currentX / CELL_SIZE) * CELL_SIZE + 32f;
        float snappedY = ((int) currentY / CELL_SIZE) * CELL_SIZE + 32f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Если строить можно — подсвечиваем клетку зеленым, если нельзя (дорога/занято) — красным
        if (gameLoop.getGameGrid().isCellBuildable(snappedX, snappedY) && isCellFree(snappedX, snappedY)) {
            shapeRenderer.setColor(new Color(0, 1, 0, 0.4f));
        } else {
            shapeRenderer.setColor(new Color(1, 0, 0, 0.4f));
        }

        // Рисуем квадрат ячейки под сетку
        shapeRenderer.rect(snappedX - 32, snappedY - 32, CELL_SIZE, CELL_SIZE);
        shapeRenderer.end();

        // Рисуем радиус атаки будущей башни
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.3f));
        shapeRenderer.circle(snappedX, snappedY, draggingType.getBaseRange());
        shapeRenderer.end();
    }

    public boolean isDragging() { return isDragging; }
    public TowerType getDraggingType() { return draggingType; }
    public float getCurrentX() { return currentX; }
    public float getCurrentY() { return currentY; }
}
