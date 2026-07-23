package io.github.simple_game.core.model.entity;

import io.github.simple_game.core.model.movement.MovementBehavior;
import io.github.simple_game.core.service.CurrencyManager;

/**
 * Класс, представляющий вражеского юнита в игровом мире.
 * Хранит состояние здоровья, скорость и делегирует логику своего
 * перемещения выбранной стратегии движения с учетом игровой экономики.
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
     * Перегруженный метод обновления состояния врага каждый кадр.
     * Если враг активен, метод продвигает его по карте с помощью установленной стратегии движения.
     * Если после хода стратегия пометила врага неактивным из-за достижения финиша, списывает жизнь базы.
     *
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     * @param economy   ссылка на менеджер экономики для обработки штрафа при прорыве врага
     */
    public void update(float deltaTime, CurrencyManager economy) {
        if (!active) return;

        if (movementBehavior != null) {
            movementBehavior.move(this, deltaTime);
        }

        if (!active && health > 0) {
            economy.decreaseLives(1);
        }
    }

    /**
     * Базовый метод обновления без параметров.
     * Оставлен пустым в соответствии с контрактом базового класса {@link Entity},
     * так как логика врага требует обязательной передачи контекста экономики.
     *
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     */
    @Override
    public void update(float deltaTime) {
        // Оставлен пустым, так как необходим вызов перегруженного метода update
    }

    /**
     * Вызывается из стратегии перемещения, когда враг успешно добирается до конца маршрута.
     * Помечает сущность как неактивную для последующего удаления из игрового цикла.
     */
    public void onReachedEnd() {
        this.active = false;
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
     * враг помечается как неактивный, а игроку начисляется награда.
     *
     * @param damage  количество наносимого урона
     * @param economy ссылка на менеджер экономики для зачисления золота за убийство
     */
    public void takeDamage(float damage, CurrencyManager economy) {
        if (!active) return;

        this.health -= damage;
        if (this.health <= 0) {
            this.active = false;
            economy.addGold(20);
        }
    }
}
