package io.github.simple_game.core.model.entity.enemy;

import io.github.simple_game.core.model.entity.Entity;
import io.github.simple_game.core.model.movement.MovementBehavior;
import io.github.simple_game.core.service.CurrencyManager;

/**
 * Абстрактный базовый класс для всех вражеских юнитов в игре.
 * Хранит общее состояние здоровья, логику получения урона, замедления и тир сложности.
 */
public abstract class Enemy extends Entity {
    protected float health;
    protected float speed;
    protected int goldReward;
    protected EnemyTier tier;

    private boolean active = true;
    private MovementBehavior movementBehavior;
    private float slowFactor = 1.0f;
    private float slowTimer = 0f;

    public Enemy(float x, float y) {
        super(x, y);
    }

    public void update(float deltaTime, CurrencyManager economy) {
        if (!active) return;

        if (slowTimer > 0) {
            slowTimer -= deltaTime;
            if (slowTimer <= 0) {
                slowFactor = 1.0f;
            }
        }

        if (movementBehavior != null) {
            movementBehavior.move(this, deltaTime);
        }

        if (!active && health > 0) {
            economy.decreaseLives(1);
        }
    }

    @Override
    public void update(float deltaTime) {
        // Оставлен пустым, так как необходим перегруженный update с экономикой
    }

    /**
     * Наносит врагу урон. Если здоровье падает до нуля или ниже,
     * враг погибает, а игроку начисляется награда, зависящая от класса монстра.
     */
    public void takeDamage(float damage, CurrencyManager economy) {
        if (!active) return;

        this.health -= damage;
        if (this.health <= 0) {
            this.active = false;
            // Динамически зачисляем золото на основе параметров подкласса (Гоблин/Зомби/Орк)
            economy.addGold(goldReward);
        }
    }

    public void applySlow(float factor, float duration) {
        this.slowFactor = factor;
        this.slowTimer = duration;
    }

    public void onReachedEnd() {
        this.active = false;
    }

    // Геттеры и сеттеры
    public float getSpeed() { return speed * slowFactor; }
    public void setSpeed(float speed) { this.speed = speed; }

    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = health; }

    public int getGoldReward() { return goldReward; }
    public EnemyTier getTier() { return tier; }
    public boolean isActive() { return active; }

    public void setMovementBehavior(MovementBehavior behavior) {
        this.movementBehavior = behavior;
    }
}
