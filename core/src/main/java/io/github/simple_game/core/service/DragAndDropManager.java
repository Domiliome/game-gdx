package io.github.simple_game.core.service;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.simple_game.core.model.entity.tower.ArcherTower;
import io.github.simple_game.core.model.entity.tower.CannonTower;
import io.github.simple_game.core.model.entity.tower.MagicTower;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.entity.tower.TowerType;

public class DragAndDropManager {
    private final GameLoop gameLoop;
    private final Viewport worldViewport;
    private final Vector3 screenTouch = new Vector3();
    private TowerType draggingType = null;
    private Tower previewTower = null;
    private boolean isDragging = false;
    private float currentX, currentY;
    private static final int CELL_SIZE = 64;

    public DragAndDropManager(GameLoop gameLoop, Viewport worldViewport) {
        this.gameLoop = gameLoop;
        this.worldViewport = worldViewport;
    }

    public void startDrag(TowerType type, float screenX, float screenY) {
        this.draggingType = type;
        this.isDragging = true;
        this.previewTower = switch (type) {
            case ARCHER -> new ArcherTower(0, 0, gameLoop);
            case CANNON -> new CannonTower(0, 0, gameLoop);
            case MAGIC  -> new MagicTower(0, 0, gameLoop);
        };
        updatePosition(screenX, screenY);
    }

    public void updatePosition(float screenX, float screenY) {
        if (!isDragging) return;
        screenTouch.set(screenX, screenY, 0);
        worldViewport.unproject(screenTouch);
        this.currentX = screenTouch.x;
        this.currentY = screenTouch.y;
    }

    public void stopDragAndPlace() {
        if (!isDragging) return;
        isDragging = false;

        float snappedX = MathUtils.floor(currentX / CELL_SIZE) * CELL_SIZE + 32f;
        float snappedY = MathUtils.floor(currentY / CELL_SIZE) * CELL_SIZE + 32f;
        CurrencyManager economy = gameLoop.getCurrencyManager();

        if (gameLoop.getGameGrid().isCellBuildable(snappedX, snappedY) && isCellFree(snappedX, snappedY)) {
            if (economy.spendGold(draggingType.getCost())) {
                Tower towerToPlace = switch (draggingType) {
                    case ARCHER -> new ArcherTower(snappedX, snappedY, gameLoop);
                    case CANNON -> new CannonTower(snappedX, snappedY, gameLoop);
                    case MAGIC  -> new MagicTower(snappedX, snappedY, gameLoop);
                };
                gameLoop.addTower(towerToPlace);
            }
        }
        draggingType = null;
        previewTower = null; // Очищаем ссылку для GC
    }

    public void drawPreview(ShapeRenderer shapeRenderer) {
        if (!isDragging || draggingType == null || previewTower == null) return;

        float snappedX = MathUtils.floor(currentX / CELL_SIZE) * CELL_SIZE + 32f;
        float snappedY = MathUtils.floor(currentY / CELL_SIZE) * CELL_SIZE + 32f;

        // 1. Отрисовка закрашенного квадрата (подсветка доступности клетки)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (gameLoop.getGameGrid().isCellBuildable(snappedX, snappedY) && isCellFree(snappedX, snappedY)) {
            shapeRenderer.setColor(new Color(0, 1, 0, 0.3f)); // Полупрозрачный зеленый
        } else {
            shapeRenderer.setColor(new Color(1, 0, 0, 0.3f)); // Полупрозрачный красный
        }
        shapeRenderer.rect(snappedX - 32, snappedY - 32, CELL_SIZE, CELL_SIZE);
        shapeRenderer.end();

        // 2. Отрисовка контура радиуса атаки башни
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.3f));
        shapeRenderer.circle(snappedX, snappedY, previewTower.getAttackRange());
        shapeRenderer.end();
    }

    private boolean isCellFree(float x, float y) {
        for (Tower tower : gameLoop.getTowers()) {
            if (tower.getPosition().dst(x, y) < CELL_SIZE) return false;
        }
        return true;
    }

    public boolean isDragging() { return isDragging; }
    public TowerType getDraggingType() { return draggingType; }
    public float getCurrentX() { return currentX; }
    public float getCurrentY() { return currentY; }
}
