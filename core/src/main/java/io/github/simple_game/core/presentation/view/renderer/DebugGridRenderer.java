package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.model.entity.map.GameGrid;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.presentation.GameViewport;
import io.github.simple_game.core.service.GameLoop;

public class DebugGridRenderer {
    private final GameLoop gameLoop;

    public DebugGridRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void renderGridAndRadius(ShapeRenderer shapeRenderer, float worldHeight, boolean isDragging) {
        if (isDragging) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(new Color(0.4f, 0.4f, 0.4f, 1f));
            for (int x = 0; x <= (int) GameViewport.WIDTH; x += GameGrid.CELL_SIZE) {
                shapeRenderer.line(x, 0, x, worldHeight);
            }
            for (int y = 0; y <= worldHeight; y += GameGrid.CELL_SIZE) {
                shapeRenderer.line(0, y, GameViewport.WIDTH, y);
            }
            shapeRenderer.end();
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.2f));
        Tower selectedTower = gameLoop.getSelectedTower();
        if (!isDragging && selectedTower != null) {
            shapeRenderer.circle(
                    selectedTower.getPosition().x,
                    selectedTower.getPosition().y,
                    selectedTower.getAttackRange());
        }
        shapeRenderer.end();
    }
}
