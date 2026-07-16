package io.github.simple_game.core.model.movement;

import com.badlogic.gdx.math.Vector2;
import io.github.simple_game.core.model.entity.Enemy;

public class WalkMovement implements MovementBehavior {
    private final RoadPath roadPath;
    private int currentWaypointIndex = 0;

    public WalkMovement(RoadPath roadPath) {
        this.roadPath = roadPath;
    }

    @Override
    public void move(Enemy enemy, float deltaTime) {
        // Если путь пустой или враг уже прошел все точки, ничего не делаем
        if (roadPath.getPointCount() == 0 || currentWaypointIndex >= roadPath.getPointCount()) {
            return;
        }

        // Получаем координату целевой точки
        Vector2 targetPoint = roadPath.getPoint(currentWaypointIndex);
        Vector2 enemyPos = enemy.getPosition();

        // Находим вектор направления к цели: (Цель - Текущая_Позиция)
        Vector2 direction = new Vector2(targetPoint).sub(enemyPos);

        // Расстояние до целевой точки
        float distance = direction.len();

        // Вычисляем, какое расстояние враг может пройти за этот кадр
        float step = enemy.getSpeed() * deltaTime;

        if (step >= distance) {
            // Если за один шаг мы доходим или перешагиваем точку:
            // Ставим врага ровно в точку, чтобы не было микро-подергиваний
            enemyPos.set(targetPoint);
            // Переключаемся на следующий вейпоинт
            currentWaypointIndex++;

            // Если это была последняя точка — враг дошел до конца (нанес урон базе)
            if (currentWaypointIndex >= roadPath.getPointCount()) {
                enemy.onReachedEnd();
            }
        } else {
            // Нормализуем вектор (делаем длину равной 1) и умножаем на шаг
            direction.nor().scl(step);
            // Сдвигаем позицию врага
            enemyPos.add(direction);
        }
    }
}
