package io.github.simple_game.core.service;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.simple_game.core.model.entity.map.GameGrid;
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

    public DragAndDropManager(GameLoop gameLoop, Viewport worldViewport) {
        this.gameLoop = gameLoop;
        this.worldViewport = worldViewport;
    }

    public void startDrag(TowerType type, float screenX, float screenY) {
        this.draggingType = type;
        this.isDragging = true;
        this.previewTower = type.create(0, 0, gameLoop);
        updatePosition(screenX, screenY);
    }

    public void updatePosition(float screenX, float screenY) {
        if (!isDragging) return;
        screenTouch.set(screenX, screenY, 0);
        worldViewport.unproject(screenTouch);
        this.currentX = screenTouch.x;
        this.currentY = screenTouch.y;
    }

    public boolean stopDragAndPlace() {
        if (!isDragging) return false;
        isDragging = false;
        float snappedX = GameGrid.snap(currentX);
        float snappedY = GameGrid.snap(currentY);
        CurrencyManager economy = gameLoop.getCurrencyManager();
        boolean placed = false;
        if (gameLoop.getGameGrid().isCellBuildable(snappedX, snappedY, gameLoop)) {
            if (economy.spendGold(draggingType.getCost())) {
                gameLoop.addTower(draggingType.create(snappedX, snappedY, gameLoop));
                placed = true;
            }
        }
        draggingType = null;
        previewTower = null;
        return placed;
    }

    public void drawPreview(ShapeRenderer shapeRenderer) {
        if (!isDragging || draggingType == null || previewTower == null) return;
        float snappedX = GameGrid.snap(currentX);
        float snappedY = GameGrid.snap(currentY);
        float half = GameGrid.CELL_SIZE / 2f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (gameLoop.getGameGrid().isCellBuildable(snappedX, snappedY, gameLoop)) {
            shapeRenderer.setColor(new Color(0, 1, 0, 0.3f));
        } else {
            shapeRenderer.setColor(new Color(1, 0, 0, 0.3f));
        }
        shapeRenderer.rect(snappedX - half, snappedY - half, GameGrid.CELL_SIZE, GameGrid.CELL_SIZE);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.3f));
        shapeRenderer.circle(snappedX, snappedY, previewTower.getAttackRange());
        shapeRenderer.end();
    }

    public boolean isDragging() { return isDragging; }
    public TowerType getDraggingType() { return draggingType; }
    public float getCurrentX() { return currentX; }
    public float getCurrentY() { return currentY; }
}
