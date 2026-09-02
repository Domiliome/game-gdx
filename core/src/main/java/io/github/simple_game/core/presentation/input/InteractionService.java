package io.github.simple_game.core.presentation.input;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.simple_game.core.model.entity.map.GameGrid;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.service.GameLoop;

public class InteractionService extends GestureDetector.GestureAdapter {
    private final GameLoop gameLoop;
    private final Viewport worldViewport;
    private final DragAndDropManager dragAndDropManager;
    private final Vector3 touchPoint = new Vector3();

    public InteractionService(GameLoop gameLoop, Viewport worldViewport) {
        this.gameLoop = gameLoop;
        this.worldViewport = worldViewport;
        this.dragAndDropManager = new DragAndDropManager(gameLoop, worldViewport);
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        touchPoint.set(x, y, 0);
        worldViewport.unproject(touchPoint);

        float snappedX = GameGrid.snap(touchPoint.x);
        float snappedY = GameGrid.snap(touchPoint.y);

        for (Tower tower : gameLoop.getTowers()) {
            if (tower.getPosition().dst(snappedX, snappedY) < GameGrid.CELL_SIZE) {
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
