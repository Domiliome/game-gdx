package io.github.simple_game.core.model.entity.map;

import com.badlogic.gdx.math.Vector2;

import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.presentation.GameViewport;
import io.github.simple_game.core.service.GameLoop;

public class GameGrid {
    /** Размер клетки застройки в мировых координатах. */
    public static final int CELL_SIZE = 48;

    private final RoadPath roadPath;

    public GameGrid(RoadPath roadPath) {
        this.roadPath = roadPath;
    }

    public static float snap(float coordinate) {
        return (float) (Math.floor(coordinate / CELL_SIZE) * CELL_SIZE + (CELL_SIZE / 2f));
    }

    public static float cellCenterX(int col) {
        return col * CELL_SIZE + CELL_SIZE / 2f;
    }

    public static float cellCenterY(int row) {
        return row * CELL_SIZE + CELL_SIZE / 2f;
    }

    public static int columnCount() {
        return (int) (GameViewport.WIDTH / CELL_SIZE);
    }

    /** Центральная колонка карты — все координаты пути должны проходить через неё. */
    public static float mapCenterX() {
        return cellCenterX(columnCount() / 2);
    }

    public static int worldToCol(float x) {
        return (int) Math.floor(x / CELL_SIZE);
    }

    public static int worldToRow(float y) {
        return (int) Math.floor(y / CELL_SIZE);
    }

    /** Помечает клетки, через которые проходит ось-выровненный путь (Manhattan). */
    public static void fillRoadMask(boolean[][] isRoad, RoadPath roadPath) {
        int cols = isRoad.length;
        int rows = isRoad[0].length;

        for (int i = 0; i < roadPath.getPointCount() - 1; i++) {
            Vector2 p1 = roadPath.getPoint(i);
            Vector2 p2 = roadPath.getPoint(i + 1);
            int c1 = worldToCol(p1.x);
            int r1 = worldToRow(p1.y);
            int c2 = worldToCol(p2.x);
            int r2 = worldToRow(p2.y);

            if (r1 == r2) {
                for (int c = Math.min(c1, c2); c <= Math.max(c1, c2); c++) {
                    if (c >= 0 && c < cols && r1 >= 0 && r1 < rows) {
                        isRoad[c][r1] = true;
                    }
                }
            } else if (c1 == c2) {
                for (int r = Math.min(r1, r2); r <= Math.max(r1, r2); r++) {
                    if (c1 >= 0 && c1 < cols && r >= 0 && r < rows) {
                        isRoad[c1][r] = true;
                    }
                }
            }
        }
    }

    public static boolean isRoadCell(int col, int row, RoadPath roadPath) {
        for (int i = 0; i < roadPath.getPointCount() - 1; i++) {
            Vector2 p1 = roadPath.getPoint(i);
            Vector2 p2 = roadPath.getPoint(i + 1);
            int c1 = worldToCol(p1.x);
            int r1 = worldToRow(p1.y);
            int c2 = worldToCol(p2.x);
            int r2 = worldToRow(p2.y);

            if (r1 == r2 && row == r1 && col >= Math.min(c1, c2) && col <= Math.max(c1, c2)) {
                return true;
            }
            if (c1 == c2 && col == c1 && row >= Math.min(r1, r2) && row <= Math.max(r1, r2)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCellBuildable(float snappedX, float snappedY, GameLoop gameLoop) {
        if (snappedX < 0 || snappedX > GameViewport.WIDTH || snappedY < 0) return false;

        for (Tower existingTower : gameLoop.getTowers()) {
            float distance = existingTower.getPosition().dst(snappedX, snappedY);
            if (distance < CELL_SIZE * 1.1f) {
                return false;
            }
        }

        int col = worldToCol(snappedX);
        int row = worldToRow(snappedY);
        if (isRoadCell(col, row, roadPath)) {
            return false;
        }

        return true;
    }
}
