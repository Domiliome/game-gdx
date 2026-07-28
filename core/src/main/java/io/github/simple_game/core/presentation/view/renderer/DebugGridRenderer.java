package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.service.GameLoop;

public class DebugGridRenderer {
    private final GameLoop gameLoop;
    private static final int CELL_SIZE = 32;

    public DebugGridRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void render(ShapeRenderer shapeRenderer, float worldHeight, boolean isDragging) {
        if (isDragging) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(new Color(0.4f, 0.4f, 0.4f, 1f));
            for (int x = 0; x <= 480; x += CELL_SIZE) shapeRenderer.line(x, 0, x, worldHeight);
            for (int y = 0; y <= worldHeight; y += CELL_SIZE) shapeRenderer.line(0, y, 480, y);
            shapeRenderer.end();
        }


        RoadPath path = gameLoop.getRoadPath();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.35f, 0.35f, 0.35f, 1f));

        for (int i = 0; i < path.getPointCount() - 1; i++) {
            Vector2 p1 = path.getPoint(i);
            Vector2 p2 = path.getPoint(i + 1);


            float minX = Math.min(p1.x, p2.x) - 16f;
            float minY = Math.min(p1.y, p2.y) - 16f;

            float width = (Float.compare(p1.x, p2.x) == 0) ? CELL_SIZE : Math.abs(p1.x - p2.x) + CELL_SIZE;
            float height = (Float.compare(p1.y, p2.y) == 0) ? CELL_SIZE : Math.abs(p1.y - p2.y) + CELL_SIZE;

            shapeRenderer.rect(minX, minY, width, height);
        }
        shapeRenderer.end();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.2f));
        Tower selectedTower = gameLoop.getSelectedTower();
        if (!isDragging && selectedTower != null) {
            shapeRenderer.circle(selectedTower.getPosition().x, selectedTower.getPosition().y, selectedTower.getAttackRange());
        }
        shapeRenderer.end();
    }
}
