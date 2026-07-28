package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.Entity;
import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;

public abstract class Tower extends Entity {
    protected final TowerType type;
    protected final GameLoop gameLoop;
    protected int currentLevel = 1;
    protected int maxLevel = 5;

    protected float damage;
    protected float attackRange;
    protected float attackCooldown;
    protected float shootTimer = 0f;

    protected Enemy target;

    public Tower(float x, float y, TowerType type, GameLoop gameLoop) {
        super(x, y);
        this.type = type;
        this.gameLoop = gameLoop;
    }

    public abstract void tryUpgrade();
    public abstract int getUpgradeCost();

    public void update(float deltaTime, Array<Enemy> enemies, Array<Projectile> projectilesToSpawn) {
        checkAndFindTarget(enemies);

        if (target != null) {
            shootTimer += deltaTime;
            if (shootTimer >= attackCooldown) {
                shoot(projectilesToSpawn);
                shootTimer = 0f;
            }
        } else {
            shootTimer = attackCooldown;
        }
    }

    @Override
    public void update(float deltaTime) {
        // Логика требует перегруженного update
    }

    protected void checkAndFindTarget(Array<Enemy> enemies) {
        if (target != null && target.isActive() && position.dst(target.getPosition()) <= attackRange) {
            return;
        }

        target = null;
        for (Enemy enemy : enemies) {
            if (enemy.isActive() && position.dst(enemy.getPosition()) <= attackRange) {
                target = enemy;
                break;
            }
        }
    }

    protected abstract void shoot(Array<Projectile> projectilesToSpawn);

    // Геттеры
    public float getAttackRange() { return attackRange; }
    public float getDamage() { return damage; }
    public float getAttackCooldown() { return attackCooldown; }
    public TowerType getType() { return type; }
    public int getCurrentLevel() { return currentLevel; }

    // ВАЖНО: Сеттеры для динамического изменения параметров предметами-стратегиями
    public void setAttackRange(float attackRange) { this.attackRange = attackRange; }
    public void setDamage(float damage) { this.damage = damage; }
    public void setAttackCooldown(float attackCooldown) { this.attackCooldown = attackCooldown; }
}
