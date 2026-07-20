package io.github.simple_game.core.model.entity;

import java.util.Objects;

/**
 * Неизменяемый класс, представляющий целочисленные координаты (позицию) на игровой карте.
 * Используется преимущественно для логики работы с сеткой (Grid), тайлами или игровыми ячейками.
 */
public final class Position {

    private final int x;
    private final int y;

    /**
     * Конструктор для создания точки с заданными целочисленными координатами.
     *
     * @param x координата ячейки по горизонтали
     * @param y координата ячейки по вертикали
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return координату ячейки по горизонтали
     */
    public int getX() {
        return x;
    }

    /**
     * @return координату ячейки по вертикали
     */
    public int getY() {
        return y;
    }

    /**
     * Сравнивает текущую позицию с другим объектом.
     * Результат равен true только в том случае, если аргумент не равен null,
     * является объектом класса Position и хранит такие же координаты X и Y.
     *
     * @param o объект для сравнения
     * @return true, если координаты объектов полностью совпадают; иначе false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    /**
     * Возвращает хэш-код для текущего объекта позиции, рассчитанный на основе координат X и Y.
     *
     * @return хэш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Возвращает строковое представление объекта, содержащее значения координат X и Y.
     *
     * @return строковое представление позиции
     */
    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
