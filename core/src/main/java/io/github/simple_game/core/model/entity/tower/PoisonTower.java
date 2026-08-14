package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.projectile.PoisonBolt;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;

/**
 * Ядовитая башня. Слабый разовый удар, но сильный урон со временем (DoT).
 */
public class PoisonTower extends Tower {
    private static final float BASE_DAMAGE = 5f;
    private static final float BASE_RANGE = 140f;
    private static final float BASE_COOLDOWN = 1.5f;
    private static final float BASE_POISON_DPS = 8f;
    private static final float BASE_POISON_DURATION = 4f;

    private float poisonDps = BASE_POISON_DPS;
    private float poisonDuration = BASE_POISON_DURATION;

    public PoisonTower(float x, float y, GameLoop gameLoop) {
        super(x, y, TowerType.POISON, gameLoop);
        this.damage = BASE_DAMAGE;
        this.attackRange = BASE_RANGE;
        this.attackCooldown = BASE_COOLDOWN;
        loadInitAnimation("towers/poison_init.png");
    }

    @Override
    public void tryUpgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            this.damage = BASE_DAMAGE * currentLevel;
            this.attackRange = BASE_RANGE + (currentLevel - 1) * 10f;
            this.poisonDps = BASE_POISON_DPS * currentLevel;
            this.poisonDuration = BASE_POISON_DURATION + (currentLevel - 1) * 0.5f;

            this.baseDamage = this.damage;
            this.baseAttackRange = this.attackRange;
            this.baseAttackCooldown = this.attackCooldown;
            System.out.println("Ядовитая башня улучшена до уровня " + currentLevel);
        }
    }

    @Override
    public int getUpgradeCost() {
        return (int) (type.getCost() * 0.65f * currentLevel);
    }

    @Override
    protected void shoot(Array<Projectile> projectilesToSpawn) {
        Projectile bolt = new PoisonBolt(
                position.x, position.y, target, damage, type, poisonDps, poisonDuration);
        projectilesToSpawn.add(bolt);
    }
}
