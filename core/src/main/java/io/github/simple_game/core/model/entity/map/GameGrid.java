package io.github.simple_game.core.model.entity.map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.presentation.GameViewport;
import io.github.simple_game.core.service.GameLoop;

public class GameGrid {
    /** Размер клетки застройки в мировых координатах. */
    public static final int CELL_SIZE = 48;
    /** Фиксированное число колонок игрового поля. */
    public static final int COLS = (int) (GameViewport.WIDTH / CELL_SIZE);
    /** Фиксированное число рядов игрового поля. */
    public static final int ROWS = (int) (GameViewport.HEIGHT / CELL_SIZE);
    public static final int OTHER_STRAIGHT_COUNT = 3;

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
        return COLS;
    }

    public static int rowCount() {
        return ROWS;
    }

    public static float worldWidth() {
        return COLS * CELL_SIZE;
    }

    public static float worldHeight() {
        return ROWS * CELL_SIZE;
    }

    public static boolean containsCell(int col, int row) {
        return col >= 0 && col < COLS && row >= 0 && row < ROWS;
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

    /**
     * Для каждой клетки пути: 30% обычная текстура, 70% — случайный из трёх other-вариантов.
     */
    public static void fillStraightVariants(int[][] variant, RoadPath roadPath) {
        int cols = variant.length;
        int rows = variant[0].length;
        boolean[][] visited = new boolean[cols][rows];

        for (int i = 0; i < roadPath.getPointCount() - 1; i++) {
            Vector2 p1 = roadPath.getPoint(i);
            Vector2 p2 = roadPath.getPoint(i + 1);
            int c1 = worldToCol(p1.x);
            int r1 = worldToRow(p1.y);
            int c2 = worldToCol(p2.x);
            int r2 = worldToRow(p2.y);

            if (r1 == r2 && c1 != c2) {
                int step = Integer.compare(c2, c1);
                for (int c = c1; c != c2 + step; c += step) {
                    markStraightVariant(variant, visited, c, r1, cols, rows);
                }
            } else if (c1 == c2 && r1 != r2) {
                int step = Integer.compare(r2, r1);
                for (int r = r1; r != r2 + step; r += step) {
                    markStraightVariant(variant, visited, c1, r, cols, rows);
                }
            }
        }
    }

    private static void markStraightVariant(int[][] variant, boolean[][] visited,
                                            int col, int row, int cols, int rows) {
        if (col < 0 || col >= cols || row < 0 || row >= rows || visited[col][row]) {
            return;
        }
        visited[col][row] = true;
        if (MathUtils.randomBoolean(0.7f)) {
            variant[col][row] = MathUtils.random(1, OTHER_STRAIGHT_COUNT);
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
        int col = worldToCol(snappedX);
        int row = worldToRow(snappedY);
        if (!containsCell(col, row)) return false;

        for (Tower existingTower : gameLoop.getTowers()) {
            float distance = existingTower.getPosition().dst(snappedX, snappedY);
            if (distance < CELL_SIZE * 1.1f) {
                return false;
            }
        }

        if (isRoadCell(col, row, roadPath)) {
            return false;
        }

        return true;
    }
}
