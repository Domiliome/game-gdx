package io.github.simple_game.core.model.entity.map;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.service.GameLoop;

public class GameGrid {
    private final RoadPath roadPath;
    public static final int CELL_SIZE = 32; // Единый стандарт сетки

    public GameGrid(RoadPath roadPath) {
        this.roadPath = roadPath;
    }

    public boolean isCellBuildable(float snappedX, float snappedY, GameLoop gameLoop) {
        if (snappedX < 0 || snappedX > 480 || snappedY < 0) return false;

        // Защита от постройки вплотную: расстояние между центрами соседних клеток ровно 32
        for (Tower existingTower : gameLoop.getTowers()) {
            float distance = existingTower.getPosition().dst(snappedX, snappedY);
            if (distance < CELL_SIZE * 1.1f) { // 1.1 гарантирует блок строго соседних ячеек
                return false;
            }
        }

        // Защита дороги: проверяем квадрат ячейки 32x32 вокруг snapped-центра
        Rectangle cellBounds = new Rectangle(snappedX - 16f, snappedY - 16f, CELL_SIZE, CELL_SIZE);
        for (int i = 0; i < roadPath.getPointCount() - 1; i++) {
            Vector2 p1 = roadPath.getPoint(i);
            Vector2 p2 = roadPath.getPoint(i + 1);
            if (Intersector.intersectSegmentRectangle(p1, p2, cellBounds)) {
                return false;
            }
        }
        return true;
    }
}
