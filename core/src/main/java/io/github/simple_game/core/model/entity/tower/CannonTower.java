package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.CombatWorld;
import io.github.simple_game.core.model.entity.projectile.CannonBall;
import io.github.simple_game.core.model.entity.projectile.Projectile;

/**
 * Артиллерийская пушка. Обладает сокрушительным разовым уроном.
 */
public class CannonTower extends Tower {
    private static final float BASE_DAMAGE = 50f;
    private static final float BASE_RANGE = 120f;
    private static final float BASE_COOLDOWN = 2.0f;

    private final float damageMultiplier = 1.45f;
    private final float rangeMultiplier = 1.08f;
    private final float cooldownReduction = 1.0f;

    public CannonTower(float x, float y, CombatWorld world) {
        super(x, y, TowerType.CANNON, world);
        this.damage = BASE_DAMAGE;
        this.attackRange = BASE_RANGE;
        this.attackCooldown = BASE_COOLDOWN;
    }

    @Override
    public void tryUpgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            this.damage = BASE_DAMAGE * (float) Math.pow(damageMultiplier, currentLevel - 1);
            this.attackRange = BASE_RANGE * (float) Math.pow(rangeMultiplier, currentLevel - 1);
            this.attackCooldown = Math.max(0.1f, BASE_COOLDOWN * (float) Math.pow(cooldownReduction, currentLevel - 1));

            this.baseDamage = this.damage;
            this.baseAttackRange = this.attackRange;
            this.baseAttackCooldown = this.attackCooldown;

            System.out.println("Пушка улучшена до уровня " + currentLevel);
        }
    }

    @Override
    public int getUpgradeCost() {
        return (int) (type.getCost() * 0.7f * currentLevel);
    }

    @Override
    protected void shoot(Array<Projectile> projectilesToSpawn) {
        Projectile cannonBall = new CannonBall(position.x, position.y, target, damage, type, world.getEnemies());
        projectilesToSpawn.add(cannonBall);
        System.out.println("Пушка бабахнула! Нанесено " + damage + " ед. урона по площади");
    }
}
