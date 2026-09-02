package io.github.simple_game.core.model.entity.projectile;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.tower.TowerType;

/**
 * Ядовитый снаряд: мгновенный урон при попадании и DoT на цели.
 */
public class PoisonBolt extends Projectile {
    private final float poisonDps;
    private final float poisonDuration;

    public PoisonBolt(float x, float y, Enemy target, float damage, TowerType towerType,
                      float poisonDps, float poisonDuration) {
        super(x, y, target, damage, towerType);
        this.poisonDps = poisonDps;
        this.poisonDuration = poisonDuration;
    }

    @Override
    protected void hitTarget() {
        target.applyPoison(poisonDps, poisonDuration);
        super.hitTarget();
    }
}
