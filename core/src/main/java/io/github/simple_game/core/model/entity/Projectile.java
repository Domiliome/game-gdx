package io.github.simple_game.core.model.entity;

import com.badlogic.gdx.math.Vector2;

public class Projectile extends Entity {
    private final Enemy target;
    private final float damage;
    private final float speed;
    private boolean active = true;

    public Projectile(float x, float y, Enemy target, float damage, TowerType towerType) {
        super(x, y);
        this.target = target;
        this.damage = damage;

        // Скорость снаряда зависит от типа башни
        this.speed = determineSpeed(towerType);
    }

    private float determineSpeed(TowerType towerType) {
        switch (towerType) {
            case ARCHER: return 400f; // Стрела летит быстро
            case CANNON: return 250f; // Ядро летит медленнее
            case MAGIC:  return 320f; // Магический заряд
            default:     return 300f;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (!active) return;

        // 1. Если цель погибла или ушла с карты до того, как снаряд долетел
        if (target == null || !target.isActive()) {
            active = false;
            return;
        }

        // 2. Вычисляем вектор направления к цели: (Позиция_Цели - Позиция_Снаряда)
        Vector2 targetPos = target.getPosition();
        Vector2 direction = new Vector2(targetPos).sub(position);

        // Расстояние до цели
        float distance = direction.len();

        // Дистанция, которую снаряд пролетит за этот кадр
        float step = speed * deltaTime;

        // 3. Проверяем столкновение (попадание)
        // Если шаг больше или равен расстоянию, значит в этом кадре мы долетели
        if (step >= distance) {
            position.set(targetPos); // Перемещаем точно в цель
            hitTarget();
        } else {
            // Нормализуем вектор (длина = 1) и продвигаем снаряд вперед
            direction.nor().scl(step);
            position.add(direction);
        }
    }

    /**
     * Логика при попадании снаряда во врага
     */
    private void hitTarget() {
        active = false; // Удаляем снаряд из игры
        target.takeDamage(damage); // Наносим урон врагу
    }

    public boolean isActive() {
        return active;
    }
}
