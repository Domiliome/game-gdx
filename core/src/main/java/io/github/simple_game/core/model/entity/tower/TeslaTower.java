package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.CurrencyManager;
import io.github.simple_game.core.service.GameLoop;

/**
 * Тесла-столб. Одна башня ничего не делает: нужна пара в радиусе связи.
 * Между связанными столбами идёт молния, которая периодически бьёт врагов на линии.
 */
public class TeslaTower extends Tower {
    private static final float BASE_DAMAGE = 12f;
    private static final float BASE_LINK_RANGE = 200f;
    private static final float BASE_COOLDOWN = 0.4f;
    private static final float BEAM_HALF_WIDTH = 10f;

    private TeslaTower partner;
    private float beamTimer = 0f;

    public TeslaTower(float x, float y, GameLoop gameLoop) {
        super(x, y, TowerType.TESLA, gameLoop);
        this.damage = BASE_DAMAGE;
        // attackRange = радиус поиска партнёра (круг при выборе башни)
        this.attackRange = BASE_LINK_RANGE;
        this.attackCooldown = BASE_COOLDOWN;
        loadInitAnimation("towers/tesla_init.png");
    }

    @Override
    protected void updateCombat(float deltaTime, Array<Enemy> enemies, Array<Projectile> projectilesToSpawn) {
        refreshPartnerLink();

        if (partner == null || !isBeamPrimary()) {
            return;
        }

        beamTimer += deltaTime;
        if (beamTimer >= attackCooldown) {
            beamTimer = 0f;
            damageEnemiesOnBeam(enemies);
        }
    }

    private void refreshPartnerLink() {
        if (partner != null) {
            if (!isValidPartner(partner)) {
                clearPartner();
            } else {
                return;
            }
        }

        TeslaTower nearest = null;
        float nearestDist = Float.MAX_VALUE;

        Array<Tower> towers = gameLoop.getTowers();
        for (int i = 0; i < towers.size; i++) {
            Tower tower = towers.get(i);
            if (tower == this || !(tower instanceof TeslaTower candidate)) {
                continue;
            }
            if (candidate.isInitializing()) {
                continue;
            }
            if (candidate.partner != null && candidate.partner != this) {
                continue;
            }

            float dist = position.dst(candidate.getPosition());
            if (dist <= attackRange && dist < nearestDist) {
                nearestDist = dist;
                nearest = candidate;
            }
        }

        if (nearest != null) {
            this.partner = nearest;
            nearest.partner = this;
        }
    }

    private boolean isValidPartner(TeslaTower other) {
        if (!gameLoop.getTowers().contains(other, true)) {
            return false;
        }
        if (other.isInitializing()) {
            return false;
        }
        float maxRange = Math.min(this.attackRange, other.attackRange);
        return position.dst(other.getPosition()) <= maxRange;
    }

    private void clearPartner() {
        if (partner != null) {
            TeslaTower old = partner;
            partner = null;
            if (old.partner == this) {
                old.partner = null;
            }
        }
    }

    /** Чтобы урон не начислялся дважды — бьёт только «старший» из пары. */
    private boolean isBeamPrimary() {
        return System.identityHashCode(this) < System.identityHashCode(partner);
    }

    private void damageEnemiesOnBeam(Array<Enemy> enemies) {
        Vector2 a = position;
        Vector2 b = partner.getPosition();
        CurrencyManager economy = gameLoop.getCurrencyManager();

        for (int i = 0; i < enemies.size; i++) {
            Enemy enemy = enemies.get(i);
            if (!enemy.isActive()) {
                continue;
            }
            float dist = Intersector.distanceSegmentPoint(
                    a.x, a.y, b.x, b.y,
                    enemy.getPosition().x, enemy.getPosition().y);
            if (dist <= BEAM_HALF_WIDTH) {
                enemy.takeDamage(damage, economy);
            }
        }
    }

    @Override
    public void tryUpgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            this.damage = BASE_DAMAGE + (currentLevel - 1) * 5f;
            this.attackRange = BASE_LINK_RANGE + (currentLevel - 1) * 20f;
            this.attackCooldown = Math.max(0.2f, BASE_COOLDOWN - (currentLevel - 1) * 0.03f);

            this.baseDamage = this.damage;
            this.baseAttackRange = this.attackRange;
            this.baseAttackCooldown = this.attackCooldown;
            System.out.println("Тесла улучшена до уровня " + currentLevel);
        }
    }

    @Override
    public int getUpgradeCost() {
        return (int) (type.getCost() * 0.6f * currentLevel);
    }

    @Override
    protected void shoot(Array<Projectile> projectilesToSpawn) {
        // Тесла не стреляет снарядами — урон идёт от молнии между парой.
    }

    @Override
    public void onRemoved() {
        clearPartner();
    }

    public boolean hasPartner() {
        return partner != null && isValidPartner(partner);
    }

    public TeslaTower getPartner() {
        return partner;
    }

    /** Рисуем луч только с primary, чтобы не дублировать линию. */
    public boolean shouldDrawBeam() {
        return hasPartner() && isBeamPrimary();
    }
}
