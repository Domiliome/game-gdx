package io.github.simple_game.core.model.entity.enemy;

import com.badlogic.gdx.math.Vector2;

import io.github.simple_game.core.model.entity.Entity;
import io.github.simple_game.core.model.movement.MovementBehavior;

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
    private float poisonDps = 0f;
    private float poisonTimer = 0f;

    private float animationTime = 0f;
    private final Vector2 lastPosition = new Vector2();
    private float currentRotation = 0f;
    private float visualScale = 1f;

    public Enemy(float x, float y) {
        super(x, y);
        lastPosition.set(x, y);
    }

    protected void initSprite(float visualScale) {
        this.visualScale = visualScale;
    }

    public abstract String getSpritePath();

    protected float getDefaultVisualScale() {
        return 1f;
    }

    public void update(float deltaTime) {
        if (!active) return;

        if (slowTimer > 0) {
            slowTimer -= deltaTime;
            if (slowTimer <= 0) {
                slowFactor = 1.0f;
            }
        }

        if (poisonTimer > 0) {
            takeDamage(poisonDps * deltaTime);
            if (!active) return;
            poisonTimer -= deltaTime;
            if (poisonTimer <= 0) {
                poisonDps = 0f;
            }
        }

        if (movementBehavior != null) {
            movementBehavior.move(this, deltaTime);
        }

        if (isActive()) {
            animationTime += deltaTime;
            updateFacing();
        }
    }

    /**
     * Наносит врагу урон. Если здоровье падает до нуля или ниже, враг погибает.
     * Награда и лут начисляются игровым циклом при удалении с карты.
     */
    public void takeDamage(float damage) {
        if (!active) return;

        this.health -= damage;
        if (this.health <= 0) {
            this.active = false;
        }
    }

    public void applySlow(float factor, float duration) {
        this.slowFactor = factor;
        this.slowTimer = duration;
    }

    /**
     * Накладывает яд: периодический урон (DPS) на заданную длительность.
     * Более сильный яд заменяет текущий; таймер берётся как максимум из двух.
     */
    public void applyPoison(float dps, float duration) {
        if (dps >= poisonDps) {
            this.poisonDps = dps;
        }
        this.poisonTimer = Math.max(this.poisonTimer, duration);
    }

    public boolean isPoisoned() {
        return poisonTimer > 0 && poisonDps > 0;
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

    private void updateFacing() {
        float deltaX = position.x - lastPosition.x;
        float deltaY = position.y - lastPosition.y;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (deltaX > 0.1f) {
                currentRotation = 90f;
            } else if (deltaX < -0.1f) {
                currentRotation = 270f;
            }
        } else {
            if (deltaY > 0.1f) {
                currentRotation = 180f;
            } else if (deltaY < -0.1f) {
                currentRotation = 0f;
            }
        }

        lastPosition.set(position.x, position.y);
    }

    public float getAnimationTime() {
        return animationTime;
    }

    public float getCurrentRotation() {
        return currentRotation;
    }

    public float getVisualScale() {
        return visualScale;
    }
}
