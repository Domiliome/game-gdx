package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.CombatWorld;
import io.github.simple_game.core.model.entity.projectile.MagicSphere;
import io.github.simple_game.core.model.entity.projectile.Projectile;

public class MagicTower extends Tower {
    private static final float BASE_DAMAGE = 10f;
    private static final float BASE_RANGE = 130f;
    private static final float BASE_COOLDOWN = 1.2f;

    public MagicTower(float x, float y, CombatWorld world) {
        super(x, y, TowerType.MAGIC, world);
        this.damage = BASE_DAMAGE;
        this.attackRange = BASE_RANGE;
        this.attackCooldown = BASE_COOLDOWN;
    }

    @Override
    public void tryUpgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            this.damage = BASE_DAMAGE * currentLevel;
            this.attackRange = BASE_RANGE + (currentLevel - 1) * 15f;
            this.baseDamage = this.damage;
            this.baseAttackRange = this.attackRange;
            this.baseAttackCooldown = this.attackCooldown;
            System.out.println("Магическая башня улучшена до уровня " + currentLevel);
        }
    }

    @Override
    public int getUpgradeCost() {
        return (int) (type.getCost() * 0.8f * currentLevel);
    }

    @Override
    protected void shoot(Array<Projectile> projectilesToSpawn) {
        Projectile sphere = new MagicSphere(position.x, position.y, target, damage, type);
        projectilesToSpawn.add(sphere);
    }
}
