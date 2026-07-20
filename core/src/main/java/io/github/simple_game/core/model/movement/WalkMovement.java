package io.github.simple_game.core.model.movement;

import com.badlogic.gdx.math.Vector2;

import io.github.simple_game.core.model.entity.Enemy;

/**
 * Конкретная реализация стратегии перемещения, описывающая классическую ходьбу наземных юнитов.
 * Враг последовательно перемещается от одной ключевой точки маршрута (вейпоинта) к другой.
 * При достижении финальной точки пути автоматически триггерится логика проникновения на базу.
 */
public class WalkMovement implements MovementBehavior {
    private final RoadPath roadPath;
    private int currentWaypointIndex = 0;

    /**
     * Создает стратегию ходьбы, привязанную к конкретному маршруту уровня.
     *
     * @param roadPath маршрут движения, по которому должен следовать юнит
     */
    public WalkMovement(RoadPath roadPath) {
        this.roadPath = roadPath;
    }

    /**
     * Пересчитывает позицию вражеского юнита, плавно сдвигая его к текущей целевой точке пути.
     * Если расчетный шаг за кадр превышает оставшееся расстояние до вейпоинта, юнит жестко
     * позиционируется в целевую точку, а индекс маршрута инкрементируется для устранения микро-подергиваний.
     *
     * @param enemy     объект врага, чьи координаты обновляются
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     */
    @Override
    public void move(Enemy enemy, float deltaTime) {
        if (roadPath.getPointCount() == 0 || currentWaypointIndex >= roadPath.getPointCount()) {
            return;
        }

        Vector2 targetPoint = roadPath.getPoint(currentWaypointIndex);
        Vector2 enemyPos = enemy.getPosition();

        Vector2 direction = new Vector2(targetPoint).sub(enemyPos);
        float distance = direction.len();
        float step = enemy.getSpeed() * deltaTime;

        if (step >= distance) {
            enemyPos.set(targetPoint);
            currentWaypointIndex++;

            if (currentWaypointIndex >= roadPath.getPointCount()) {
                enemy.onReachedEnd();
            }
        } else {
            direction.nor().scl(step);
            enemyPos.add(direction);
        }
    }
}
