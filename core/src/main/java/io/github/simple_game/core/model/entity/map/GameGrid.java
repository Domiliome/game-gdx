package io.github.simple_game.core.model.entity.map;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.service.GameLoop;

/**
 * Логическая сетка карты. Рассчитывает доступность ячеек 32x32 для строительства.
 * Полностью запрещает возведение башен на дороге и вплотную к другим башням.
 */
public class GameGrid {
    private final RoadPath roadPath;
    private static final int CELL_SIZE = 32; // Размер одной ячейки сетки

    public GameGrid(RoadPath roadPath) {
        this.roadPath = roadPath;
    }

    /**
     * Проверяет, можно ли построить башню в указанных snapped-координатах.
     *
     * @param snappedX координата X центра проверяемой ячейки
     * @param snappedY координата Y центра проверяемой ячейки
     * @param gameLoop актуальная ссылка на игровой цикл для сканирования существующих башен
     * @return true, если клетка свободна для строительства; false, если заблокирована.
     */
    public boolean isCellBuildable(float snappedX, float snappedY, GameLoop gameLoop) {

        if (snappedX < 0 || snappedX > 480 || snappedY < 0) return false;


        for (Tower existingTower : gameLoop.getTowers()) {
            float distance = existingTower.getPosition().dst(snappedX, snappedY);
            if (distance < CELL_SIZE * 1.5f) {
                return false;
            }
        }


        Rectangle cellBounds = new Rectangle(snappedX - 16f, snappedY - 16f, CELL_SIZE, CELL_SIZE);

        // Сканируем все отрезки пути врагов (между вейпоинтами)
        for (int i = 0; i < roadPath.getPointCount() - 1; i++) {
            Vector2 p1 = roadPath.getPoint(i);
            Vector2 p2 = roadPath.getPoint(i + 1);

            // Проверяем, пересекает ли линия текущего отрезка дороги наш квадрат 32x32
            if (Intersector.intersectSegmentRectangle(p1, p2, cellBounds)) {
                return false; // Клетка зарезервирована под дорогу, строить НЕЛЬЗЯ!
            }
        }

        return true; // Клетка полностью свободна, строить можно
    }
}
