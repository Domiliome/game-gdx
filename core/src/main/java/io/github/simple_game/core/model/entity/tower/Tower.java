package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.Entity;
import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;

public abstract class Tower extends Entity {
    protected final TowerType type;
    protected final GameLoop gameLoop;
    protected int currentLevel = 1;
    protected int maxLevel = 5;

    // БАЗОВЫЕ характеристики (чистые, без шмоток)
    protected float baseDamage;
    protected float baseAttackRange;
    protected float baseAttackCooldown;
    protected float shootTimer = 0f;

    // ДИНАМИЧЕСКИЕ характеристики (с учетом шмоток в текущем кадре)
    protected float damage;
    protected float attackRange;
    protected float attackCooldown;

    protected Enemy target;

    public Tower(float x, float y, TowerType type, GameLoop gameLoop) {
        super(x, y);
        this.type = type;
        this.gameLoop = gameLoop;
    }

    public abstract void tryUpgrade();
    public abstract int getUpgradeCost();
    protected abstract void shoot(Array<Projectile> projectilesToSpawn);

    public void update(float deltaTime, Array<Enemy> enemies, Array<Projectile> projectilesToSpawn) {

        if (baseDamage == 0 && damage > 0) {
            baseDamage = damage;
            baseAttackRange = attackRange;
            baseAttackCooldown = attackCooldown;
        }


        this.damage = baseDamage;
        this.attackRange = baseAttackRange;
        this.attackCooldown = baseAttackCooldown;


        for (Item item : gameLoop.getInventoryManager().getEquippedSlots()) {
            item.applyEffect(this);
        }

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


    @Override public void update(float deltaTime) {}

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

    // Геттеры динамических (финальных) значений для стрельбы и рендеринга кругов
    public float getAttackRange() { return attackRange; }
    public float getDamage() { return damage; }
    public float getAttackCooldown() { return attackCooldown; }
    public TowerType getType() { return type; }
    public int getCurrentLevel() { return currentLevel; }

    // Геттеры/Сеттеры для предметов-стратегий, чтобы они модифицировали временные параметры
    public float getDynamicRange() { return attackRange; }
    public void setDynamicRange(float r) { this.attackRange = r; }
    public float getDynamicDamage() { return damage; }
    public void setDynamicDamage(float d) { this.damage = d; }
    public float getDynamicCooldown() { return attackCooldown; }
    public void setDynamicCooldown(float c) { this.attackCooldown = c; }
}
