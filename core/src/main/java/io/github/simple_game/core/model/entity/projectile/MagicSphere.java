package io.github.simple_game.core.model.entity.projectile;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.tower.TowerType;

/**
 * Магический снаряд: урон и замедление цели.
 */
public class MagicSphere extends Projectile {

    public MagicSphere(float x, float y, Enemy target, float damage, TowerType towerType) {
        super(x, y, target, damage, towerType);
    }

    @Override
    protected void hitTarget() {
        target.applySlow(0.5f, 3.0f);
        super.hitTarget();
    }
}
