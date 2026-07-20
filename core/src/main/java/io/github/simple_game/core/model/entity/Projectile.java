package io.github.simple_game.core.model.entity;

import com.badlogic.gdx.math.Vector2;

/**
 * Класс, представляющий самонаводящийся снаряд в игровом мире.
 * Снаряд выпускается оборонительной башней, каждый кадр рассчитывает
 * направление к своей цели и преследует её до момента столкновения или деактивации.
 */
public class Projectile extends Entity {
    private final Enemy target;
    private final float damage;
    private final float speed;
    private boolean active = true;

    /**
     * Создает новый снаряд в заданных координатах, направленный на конкретного врага.
     *
     * @param x          начальная координата X появления снаряда (центр башни)
     * @param y          начальная координата Y появления снаряда (центр башни)
     * @param target     вражеский юнит, выступающий целью для атаки
     * @param damage     количество урона, которое будет нанесено цели при попадании
     * @param towerType  тип башни, выпустившей снаряд (определяет скорость полета)
     */
    public Projectile(float x, float y, Enemy target, float damage, TowerType towerType) {
        super(x, y);
        this.target = target;
        this.damage = damage;
        this.speed = determineSpeed(towerType);
    }

    /**
     * Расчитывает скорость полета снаряда на основе типа башни, которая его выпустила.
     *
     * @param towerType тип башни
     * @return скорость перемещения снаряда в пикселях в секунду
     */
    private float determineSpeed(TowerType towerType) {
        return switch (towerType) {
            case ARCHER -> 400f;
            case CANNON -> 250f;
            case MAGIC  -> 320f;
            default     -> 300f;
        };
    }

    /**
     * Обновляет физику полета снаряда каждый кадр.
     * Если цель пропадает или погибает раньше времени, снаряд деактивируется.
     * В остальных случаях снаряд летит к координатам врага с учетом игрового времени.
     *
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     */
    @Override
    public void update(float deltaTime) {
        if (!active) return;

        if (target == null || !target.isActive()) {
            active = false;
            return;
        }

        Vector2 targetPos = target.getPosition();
        Vector2 direction = new Vector2(targetPos).sub(position);
        float distance = direction.len();
        float step = speed * deltaTime;

        if (step >= distance) {
            position.set(targetPos);
            hitTarget();
        } else {
            direction.nor().scl(step);
            position.add(direction);
        }
    }

    /**
     * Внутренний метод обработки успешного столкновения снаряда с целью.
     * Наносит урон врагу и переводит снаряд в неактивное состояние для удаления из игры.
     */
    private void hitTarget() {
        active = false;
        target.takeDamage(damage);
    }

    /**
     * @return true, если снаряд все еще летит к цели; false, если он уже попал или пропал
     */
    public boolean isActive() {
        return active;
    }
}
