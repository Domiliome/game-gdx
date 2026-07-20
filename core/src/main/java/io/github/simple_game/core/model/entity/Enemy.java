package io.github.simple_game.core.model.entity;

import io.github.simple_game.core.model.movement.MovementBehavior;

/**
 * Класс, представляющий вражеского юнита в игровом мире.
 * Хранит состояние здоровья, скорость и делегирует логику своего
 * перемещения выбранной стратегии движения.
 */
public class Enemy extends Entity {
    private float health;
    private final float speed;
    private boolean active = true;
    private final MovementBehavior movementBehavior;

    /**
     * Создает нового врага с заданными характеристиками.
     *
     * @param x                начальная координата X на игровой карте
     * @param y                начальная координата Y на игровой карте
     * @param health           стартовое количество очков здоровья (HP)
     * @param speed            базовая скорость перемещения пикселей в секунду
     * @param behavior         реализация стратегии перемещения врага
     */
    public Enemy(float x, float y, float health, float speed, MovementBehavior behavior) {
        super(x, y);
        this.health = health;
        this.speed = speed;
        this.movementBehavior = behavior;
    }

    /**
     * Обновляет состояние врага каждый кадр.
     * Если враг активен, метод продвигает его по карте с помощью установленной стратегии движения.
     *
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     */
    @Override
    public void update(float deltaTime) {
        if (!active) return;

        if (movementBehavior != null) {
            movementBehavior.move(this, deltaTime);
        }
    }

    /**
     * Вызывается, когда враг успешно добирается до конца маршрута.
     * Деактивирует сущность и логирует событие проникновения на базу.
     */
    public void onReachedEnd() {
        this.active = false;
        System.out.println("Враг добрался до базы!");
    }

    /**
     * @return базовая скорость перемещения врага
     */
    public float getSpeed() { return speed; }

    /**
     * @return текущее количество здоровья врага
     */
    public float getHealth() { return health; }

    /**
     * @return true, если враг жив и находится на карте; false, если он погиб или дошел до конца
     */
    public boolean isActive() { return active; }

    /**
     * Наносит врагу урон. Если здоровье падает до нуля или ниже,
     * враг помечается как неактивный.
     *
     * @param damage количество наносимого урона
     */
    public void takeDamage(float damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.active = false;
        }
    }
}
