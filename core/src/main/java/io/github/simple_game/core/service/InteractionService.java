package io.github.simple_game.core.service;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.simple_game.core.model.entity.tower.Tower;

public class InteractionService extends GestureDetector.GestureAdapter {
    private final GameLoop gameLoop;
    private final Viewport worldViewport;
    private final DragAndDropManager dragAndDropManager;
    private final Vector3 touchPoint = new Vector3();
    private static final int CELL_SIZE = 32;

    public InteractionService(GameLoop gameLoop, Viewport worldViewport) {
        this.gameLoop = gameLoop;
        this.worldViewport = worldViewport;
        this.dragAndDropManager = new DragAndDropManager(gameLoop, worldViewport);
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        touchPoint.set(x, y, 0);
        worldViewport.unproject(touchPoint);

        float snappedX = MathUtils.floor(touchPoint.x / CELL_SIZE) * CELL_SIZE + (CELL_SIZE / 2f);
        float snappedY = MathUtils.floor(touchPoint.y / CELL_SIZE) * CELL_SIZE + (CELL_SIZE / 2f);

        for (Tower tower : gameLoop.getTowers()) {
            if (tower.getPosition().dst(snappedX, snappedY) < CELL_SIZE) {
                gameLoop.setSelectedTower(tower);
                return true;
            }
        }

        gameLoop.setSelectedTower(null);
        return false;
    }

    public DragAndDropManager getDragAndDropManager() {
        return dragAndDropManager;
    }
}
