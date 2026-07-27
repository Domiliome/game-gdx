package io.github.simple_game.core.model.entity.map;

import com.badlogic.gdx.math.Vector2;

import io.github.simple_game.core.model.movement.RoadPath;

/**
 * Класс, представляющий двумерную логическую сетку игровой карты.
 * Отвечает за разметку типов клеток (трава, дорога) и проверку
 * возможности строительства оборонительных сооружений.
 */
public class GameGrid {
    private static final int CELL_SIZE = 64;
    private static final int COLUMNS = 8;    // 480 / 64 = 7.5 (округляем до 8 для запаса по краям)
    private static final int ROWS = 13;     // 800 / 64 = 12.5 (округляем до 13)


    // Типы клеток
    public static final int CELL_GRASS = 0;
    public static final int CELL_ROAD = 1;

    // Двумерный массив карты [строка][столбец]
    private final int[][] matrix;

    /**
     * Инициализирует пустую сетку карты, заполненную травой,
     * и автоматически размечает дорожное полотно на основе маршрута движения.
     *
     * @param roadPath текущий маршрут движения врагов для разметки дорожных клеток
     */
    public GameGrid(RoadPath roadPath) {
        this.matrix = new int[ROWS][COLUMNS];

        // 1. По умолчанию заполняем всю карту травой
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                matrix[r][c] = CELL_GRASS;
            }
        }

        // 2. Автоматически размечаем дорогу между точками вейпоинтов
        markRoadCells(roadPath);
    }

    /**
     * Сканирует отрезки пути RoadPath и заполняет промежуточные клетки типом CELL_ROAD.
     */
    private void markRoadCells(RoadPath roadPath) {
        for (int i = 0; i < roadPath.getPointCount() - 1; i++) {
            Vector2 p1 = roadPath.getPoint(i);
            Vector2 p2 = roadPath.getPoint(i + 1);

            // Переводим пиксельные координаты вейпоинтов в индексы сетки
            int startC = Math.min((int)p1.x / CELL_SIZE, (int)p2.x / CELL_SIZE);
            int endC = Math.max((int)p1.x / CELL_SIZE, (int)p2.x / CELL_SIZE);
            int startR = Math.min((int)p1.y / CELL_SIZE, (int)p2.y / CELL_SIZE);
            int endR = Math.max((int)p1.y / CELL_SIZE, (int)p2.y / CELL_SIZE);

            // Заполняем прямоугольник дороги между двумя точками (для прямых линий)
            for (int r = startR; r <= endR; r++) {
                for (int c = startC; c <= endC; c++) {
                    if (r < ROWS && c < COLUMNS) {
                        matrix[r][c] = CELL_ROAD;
                    }
                }
            }
        }
    }

    /**
     * Проверяет, свободна ли конкретная ячейка по её пиксельным координатам мира.
     *
     * @param worldX координата X в игровом мире
     * @param worldY координата Y в игровом мире
     * @return true, если клетка является травой и на ней разрешено строительство; false, если это дорога
     */
    public boolean isCellBuildable(float worldX, float worldY) {
        int c = (int) worldX / CELL_SIZE;
        int r = (int) worldY / CELL_SIZE;

        // Защита от кликов за пределами карты
        if (c < 0 || c >= COLUMNS || r < 0 || r >= ROWS) {
            return false;
        }

        return matrix[r][c] == CELL_GRASS;
    }
}
