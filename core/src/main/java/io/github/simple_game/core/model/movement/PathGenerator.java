package io.github.simple_game.core.model.movement;

import com.badlogic.gdx.math.MathUtils;

import io.github.simple_game.core.model.entity.map.GameGrid;

/**
 * Процедурный генератор случайных дорог в координатах сетки {@link GameGrid#CELL_SIZE}.
 * Параллельные участки пути всегда разделены минимум одной клеткой земли.
 */
public class PathGenerator {
    /** Минимальное расстояние между осями параллельных участков (1 клетка земли между дорогами). */
    private static final int MIN_TRACK_GAP = 2;
    private static final int MAX_ATTEMPTS = 32;

    private static int centerCol() {
        return GameGrid.columnCount() / 2;
    }

    private static float centerX() {
        return GameGrid.mapCenterX();
    }

    public static void generate(RoadPath path, PathType type, float worldHeight) {
        float startY = GameGrid.snap(worldHeight + GameGrid.CELL_SIZE);

        if (path.getPointCount() > 0) {
            path.getPoint(0).set(centerX(), startY);
            return;
        }

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            path.clear();
            path.addPoint(centerX(), startY);
            path.addPoint(centerX(), GameGrid.cellCenterY(14));

            switch (type) {
                case STRAIGHT_FEW_TURNS -> generateStraight(path);
                case MANY_TURNS         -> generateManyTurns(path);
                case WITH_LOOPS         -> generateWithLoops(path);
            }

            path.addPoint(centerX(), GameGrid.cellCenterY(2));
            path.addPoint(centerX(), -GameGrid.CELL_SIZE);

            if (hasValidSpacing(path, worldHeight)) {
                return;
            }
        }

        // Запасной простой маршрут без параллельных конфликтов
        path.clear();
        path.addPoint(centerX(), startY);
        path.addPoint(centerX(), GameGrid.cellCenterY(14));
        generateFallback(path);
        path.addPoint(centerX(), GameGrid.cellCenterY(2));
        path.addPoint(centerX(), -GameGrid.CELL_SIZE);
    }

    private static void generateFallback(RoadPath path) {
        int cc = centerCol();
        int leftCol = Math.max(1, cc - MIN_TRACK_GAP);
        int rightCol = Math.min(GameGrid.columnCount() - 2, cc + MIN_TRACK_GAP);
        float midY = GameGrid.cellCenterY(10);
        path.addPoint(GameGrid.cellCenterX(leftCol), GameGrid.cellCenterY(14));
        path.addPoint(GameGrid.cellCenterX(leftCol), midY);
        path.addPoint(GameGrid.cellCenterX(rightCol), midY);
        path.addPoint(GameGrid.cellCenterX(rightCol), GameGrid.cellCenterY(2));
    }

    private static void generateStraight(RoadPath path) {
        int cc = centerCol();
        int cols = GameGrid.columnCount();
        int leftMax = Math.min(3, cc - MIN_TRACK_GAP);
        int rightMin = Math.max(cols - 4, cc + MIN_TRACK_GAP);

        int leftCol = MathUtils.random(1, Math.max(1, leftMax));
        int rightCol = MathUtils.random(Math.min(cols - 2, rightMin), cols - 2);
        float midY = GameGrid.cellCenterY(MathUtils.random(8, 11));

        path.addPoint(GameGrid.cellCenterX(leftCol), GameGrid.cellCenterY(14));
        path.addPoint(GameGrid.cellCenterX(leftCol), midY);
        path.addPoint(GameGrid.cellCenterX(rightCol), midY);
        path.addPoint(GameGrid.cellCenterX(rightCol), GameGrid.cellCenterY(2));
    }

    private static void generateManyTurns(RoadPath path) {
        int cc = centerCol();
        int cols = GameGrid.columnCount();
        int leftMax = Math.min(2, cc - MIN_TRACK_GAP);
        int rightMin = Math.max(cols - 3, cc + MIN_TRACK_GAP);

        int currentRow = 14;
        boolean turnRight = false;
        while (currentRow > 4) {
            int col = turnRight
                    ? MathUtils.random(Math.min(cols - 2, rightMin), cols - 2)
                    : MathUtils.random(1, Math.max(1, leftMax));
            float targetX = GameGrid.cellCenterX(col);
            float currentY = GameGrid.cellCenterY(currentRow);
            path.addPoint(targetX, currentY);
            currentRow -= MathUtils.random(2, 3);
            path.addPoint(targetX, GameGrid.cellCenterY(currentRow));
            turnRight = !turnRight;
        }
        path.addPoint(centerX(), GameGrid.cellCenterY(currentRow));
    }

    private static void generateWithLoops(RoadPath path) {
        int cc = centerCol();
        int cols = GameGrid.columnCount();
        int pocketsCount = MathUtils.random(1, 2);
        int currentRow = 14;

        for (int i = 0; i < pocketsCount; i++) {
            boolean loopToLeft = MathUtils.randomBoolean();
            float topY = GameGrid.cellCenterY(currentRow);
            int bottomRow = currentRow - 4;
            float bottomY = GameGrid.cellCenterY(bottomRow);
            int innerCol;
            int edgeCol;

            if (loopToLeft) {
                int innerMin = MIN_TRACK_GAP;
                int innerMax = cc - MIN_TRACK_GAP;
                if (innerMax < innerMin) {
                    continue;
                }
                innerCol = MathUtils.random(innerMin, innerMax);
                edgeCol = MathUtils.random(0, innerCol - MIN_TRACK_GAP);
            } else {
                int innerMin = cc + MIN_TRACK_GAP;
                int innerMax = cols - 1 - MIN_TRACK_GAP;
                if (innerMax < innerMin) {
                    continue;
                }
                innerCol = MathUtils.random(innerMin, innerMax);
                edgeCol = MathUtils.random(innerCol + MIN_TRACK_GAP, cols - 1);
            }

            float edgeX = GameGrid.cellCenterX(edgeCol);
            float innerX = GameGrid.cellCenterX(innerCol);

            path.addPoint(centerX(), topY);
            path.addPoint(edgeX, topY);
            path.addPoint(edgeX, bottomY);
            path.addPoint(innerX, bottomY);
            path.addPoint(innerX, GameGrid.cellCenterY(bottomRow + 2));
            path.addPoint(centerX(), GameGrid.cellCenterY(bottomRow + 2));
            path.addPoint(centerX(), GameGrid.cellCenterY(bottomRow + 1));

            currentRow = bottomRow - 2;
        }

        path.addPoint(centerX(), GameGrid.cellCenterY(2));
    }

    /** Проверяет, что соседние параллельные участки не идут вплотную друг к другу. */
    static boolean hasValidSpacing(RoadPath path, float worldHeight) {
        int cols = GameGrid.columnCount();
        int rows = (int) (worldHeight / GameGrid.CELL_SIZE) + 1;
        boolean[][] road = new boolean[cols][rows];
        GameGrid.fillRoadMask(road, path);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols - 1; c++) {
                if (!road[c][r] || !road[c + 1][r]) {
                    continue;
                }
                boolean parallelAbove = r > 0 && road[c][r - 1] && road[c + 1][r - 1];
                boolean parallelBelow = r < rows - 1 && road[c][r + 1] && road[c + 1][r + 1];
                if (parallelAbove || parallelBelow) {
                    return false;
                }
            }
        }

        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows - 1; r++) {
                if (!road[c][r] || !road[c][r + 1]) {
                    continue;
                }
                boolean parallelLeft = c > 0 && road[c - 1][r] && road[c - 1][r + 1];
                boolean parallelRight = c < cols - 1 && road[c + 1][r] && road[c + 1][r + 1];
                if (parallelLeft || parallelRight) {
                    return false;
                }
            }
        }

        return true;
    }
}
