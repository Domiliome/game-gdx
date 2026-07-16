package io.github.simple_game.core.model.entity;

import io.github.simple_game.core.model.movement.MovementBehavior;

public class Enemy extends Entity {
    private float health;
    private final float speed;
    private boolean active = true;
    private final MovementBehavior movementBehavior;

    public Enemy(float x, float y, float health, float speed, MovementBehavior behavior) {
        super(x, y);
        this.health = health;
        this.speed = speed;
        this.movementBehavior = behavior;
    }

    @Override
    public void update(float deltaTime) {
        if (!active) return;

        if (movementBehavior != null) {
            movementBehavior.move(this, deltaTime);
        }
    }

    public void onReachedEnd() {
        this.active = false;
        System.out.println("Враг добрался до базы!");
    }

    public float getSpeed() { return speed; }
    public float getHealth() { return health; }
    public boolean isActive() { return active; }

    public void takeDamage(float damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.active = false;
        }
    }
}
