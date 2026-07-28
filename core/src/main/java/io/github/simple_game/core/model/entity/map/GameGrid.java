package io.github.simple_game.core.model.entity.map;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.simple_game.core.model.movement.RoadPath;

public class GameGrid {
    private final RoadPath roadPath;
    private static final int CELL_SIZE = 32;

    public GameGrid(RoadPath roadPath) {
        this.roadPath = roadPath;
    }

    public boolean isCellBuildable(float snappedX, float snappedY) {
        if (snappedX < 0 || snappedX > 480 || snappedY < 0) return false;

        // Создаем границы проверяемой клетки 32x32
        Rectangle cellBounds = new Rectangle(
            snappedX - 16f,
            snappedY - 16f,
            CELL_SIZE,
            CELL_SIZE
        );

        for (int i = 0; i < roadPath.getPointCount() - 1; i++) {
            Vector2 p1 = roadPath.getPoint(i);
            Vector2 p2 = roadPath.getPoint(i + 1);

            // Проверяем пересечение с линией пути. Так как вейпоинты идут ровно по центрам,
            // пересечение сегмента с квадратом 32х32 сработает идеально для всей ширины дороги!
            if (Intersector.intersectSegmentRectangle(p1, p2, cellBounds)) {
                return false;
            }
        }
        return true;
    }
}
