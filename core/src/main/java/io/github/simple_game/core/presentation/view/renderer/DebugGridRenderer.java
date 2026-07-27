package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.service.GameLoop;

public class DebugGridRenderer {
    private final GameLoop gameLoop;

    public DebugGridRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void render(ShapeRenderer shapeRenderer) {
        // 1. Координатная сетка
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.1f));
        for (int x = 0; x <= 480; x += 64) shapeRenderer.line(x, 0, x, 800);
        for (int y = 0; y <= 800; y += 64) shapeRenderer.line(0, y, 480, y);

        // 2. Путь врагов
        RoadPath path = (RoadPath) gameLoop.getRoadPath();
        shapeRenderer.setColor(Color.GRAY);
        for (int i = 0; i < path.getPointCount() - 1; i++) {
            shapeRenderer.line(path.getPoint(i), path.getPoint(i + 1));
        }

        // 3. Радиусы башен
        shapeRenderer.setColor(new Color(1, 1, 1, 0.2f));
        for (Tower tower : gameLoop.getTowers()) {
            shapeRenderer.circle(tower.getPosition().x, tower.getPosition().y, tower.getAttackRange());
        }
        shapeRenderer.end();
    }
}
