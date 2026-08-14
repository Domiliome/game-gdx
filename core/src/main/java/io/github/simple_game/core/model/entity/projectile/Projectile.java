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


    private final Vector2 lastTargetPos = new Vector2();

    public Projectile(float x, float y, Enemy target, float damage, TowerType towerType) {
        super(x, y);
        this.target = target;
        this.damage = damage;
        this.speed = towerType.getProjectileSpeed();
    }

    public void update(float deltaTime, CurrencyManager economy) {
        if (!active) return;

        // 1. Пока враг жив и находится на карте, постоянно записываем его координаты
        if (target != null && target.isActive()) {
            lastTargetPos.set(target.getPosition());
        }


        Vector2 currentDestination = (target != null && target.isActive())
                ? target.getPosition()
                : lastTargetPos;

        Vector2 direction = new Vector2(currentDestination).sub(position);
        float distance = direction.len();
        float step = speed * deltaTime;

        if (step >= distance) {
            position.set(currentDestination);


            if (target != null && target.isActive()) {
                hitTarget(economy);
            } else {
                active = false;
            }
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
