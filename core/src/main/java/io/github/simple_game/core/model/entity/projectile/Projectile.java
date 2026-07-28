package io.github.simple_game.core.model.entity.projectile;

import com.badlogic.gdx.math.Vector2;

import io.github.simple_game.core.model.entity.Entity;
import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.tower.TowerType;
import io.github.simple_game.core.service.CurrencyManager;

public class Projectile extends Entity {
    protected final Enemy target;
    protected final float damage;
    protected final float speed;
    protected boolean active = true;

    public Projectile(float x, float y, Enemy target, float damage, TowerType towerType) {
        super(x, y);
        this.target = target;
        this.damage = damage;
        this.speed = determineSpeed(towerType);
    }

    private float determineSpeed(TowerType towerType) {
        return switch (towerType) {
            case ARCHER -> 400f;
            case CANNON -> 250f;
            case MAGIC  -> 320f;
            default     -> 300f;
        };
    }

    public void update(float deltaTime, CurrencyManager economy) {
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
            hitTarget(economy);
        } else {
            direction.nor().scl(step);
            position.add(direction);
        }
    }


    @Override
    public void update(float deltaTime) {
        // Оставлен пустым, так как необходим вызов перегруженного метода update
    }


    protected void hitTarget(CurrencyManager economy) {
        active = false;
        target.takeDamage(damage, economy);
    }

    public boolean isActive() {
        return active;
    }
}
