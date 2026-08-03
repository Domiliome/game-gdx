package io.github.simple_game.core.model.movement;

import com.badlogic.gdx.math.MathUtils;

/**
 * Процедурный генератор случайных дорог.
 * Генерирует лабиринты и до 2-х изолированных боковых петель-карманов с чистым островком внутри.
 * Главный путь разрывается, принудительно направляя врагов через обходной маршрут.
 */
public class PathGenerator {
    private static final int CELL_SIZE = 32;
    private static final float CENTER_X = 240f;

    public static void generate(RoadPath path, PathType type, float worldHeight) {
        float startY = MathUtils.floor((worldHeight + CELL_SIZE) / CELL_SIZE) * CELL_SIZE + (CELL_SIZE / 2f);

        if (path.getPointCount() > 0) {
            path.getPoint(0).y = startY;
            return;
        }

        path.clear();
        path.addPoint(CENTER_X, startY);
        path.addPoint(CENTER_X, 688f);

        switch (type) {
            case STRAIGHT_FEW_TURNS -> generateStraight(path);
            case MANY_TURNS         -> generateManyTurns(path);
            case WITH_LOOPS          -> generateWithLoops(path);
        }

        float lastTurnY = 112f;
        path.addPoint(CENTER_X, lastTurnY);
        path.addPoint(CENTER_X, -48f);
    }

    private static void generateStraight(RoadPath path) {
        float leftX = MathUtils.random(1, 4) * CELL_SIZE + 16f;
        float rightX = MathUtils.random(10, 13) * CELL_SIZE + 16f;
        float midY = MathUtils.random(10, 14) * CELL_SIZE + 16f;
        path.addPoint(leftX, 688f);
        path.addPoint(leftX, midY);
        path.addPoint(rightX, midY);
        path.addPoint(rightX, 112f);
    }

    private static void generateManyTurns(RoadPath path) {
        float currentY = 688f;
        boolean turnRight = false;
        while (currentY > 180f) {
            float targetX = turnRight ? MathUtils.random(11, 13) * CELL_SIZE + 16f : MathUtils.random(1, 3) * CELL_SIZE + 16f;
            path.addPoint(targetX, currentY);
            currentY -= MathUtils.random(2, 4) * CELL_SIZE;
            path.addPoint(targetX, currentY);
            turnRight = !turnRight;
        }
        path.addPoint(CENTER_X, currentY);
    }

    private static void generateWithLoops(RoadPath path) {
        int pocketsCount = MathUtils.random(1, 2);
        float currentY = 688f;
        for (int i = 0; i < pocketsCount; i++) {
            boolean loopToLeft = MathUtils.randomBoolean();
            boolean isBigLoop = MathUtils.randomBoolean();
            float topY = currentY;
            float bottomY = currentY - (4f * CELL_SIZE);
            float edgeX, innerX;

            if (loopToLeft) {

                if (isBigLoop) {
                    edgeX = 48f;
                    innerX = 144f;
                } else {
                    edgeX = 112f;
                    innerX = 176f;
                }
                path.addPoint(CENTER_X, topY);
                path.addPoint(edgeX, topY);
                path.addPoint(edgeX, bottomY);
                path.addPoint(innerX, bottomY);
                path.addPoint(innerX, topY - (2f * CELL_SIZE));
                path.addPoint(CENTER_X, topY - (2f * CELL_SIZE));
                path.addPoint(CENTER_X, bottomY - CELL_SIZE);
            } else {

                if (isBigLoop) {
                    innerX = 336f;
                    edgeX = 432f;
                } else {
                    innerX = 304f;
                    edgeX = 368f;
                }
                path.addPoint(CENTER_X, topY);
                path.addPoint(edgeX, topY);
                path.addPoint(edgeX, bottomY);
                path.addPoint(innerX, bottomY);
                path.addPoint(innerX, topY - (2f * CELL_SIZE));
                path.addPoint(CENTER_X, topY - (2f * CELL_SIZE));
                path.addPoint(CENTER_X, bottomY - CELL_SIZE);
            }


            currentY = (bottomY - CELL_SIZE) - (2f * CELL_SIZE);
        }

        path.addPoint(CENTER_X, 112f);
    }
}
