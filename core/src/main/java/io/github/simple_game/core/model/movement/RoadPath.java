package io.github.simple_game.core.model.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Класс, представляющий маршрут (путь) на игровой карте, состоящий из последовательности точек.
 * Используется для задания траектории движения вражеских юнитов от точки спавна до базы игрока.
 */
public class RoadPath {
    private final Array<Vector2> points;

    /**
     * Создает новый пустой маршрут движения.
     */
    public RoadPath() {
        points = new Array<>();
    }

    /**
     * Добавляет новую ключевую точку (вейпоинт) в конец текущего маршрута.
     *
     * @param x координата точки по горизонтали
     * @param y координата точки по вертикали
     */
    public void addPoint(float x, float y) {
        points.add(new Vector2(x, y));
    }

    /**
     * Возвращает точку маршрута по её порядковому индексу.
     *
     * @param index индекс искомой точки (начиная с 0)
     * @return экземпляр {@link Vector2}, содержащий координаты X и Y заданной точки
     */
    public Vector2 getPoint(int index) {
        return points.get(index);
    }

    /**
     * Возвращает общее количество ключевых точек, из которых состоит текущий маршрут.
     *
     * @return число точек в пути
     */
    public int getPointCount() {
        return points.size;
    }
}
