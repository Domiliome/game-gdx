package io.github.simple_game.core.model.entity;

import java.util.Objects;

/**
 * Класс, представляющий координаты (позицию) на игровой карте.
 */
public final class Position {

    private final int x;
    private final int y;

    /**
     * Конструктор для создания точки с координатами.
     *
     * @param x координата по горизонтали
     * @param y координата по вертикали
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
