package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    protected float baseDamage;
    protected float baseAttackRange;
    protected float baseAttackCooldown;
    protected float shootTimer = 0f;

    protected float damage;
    protected float attackRange;
    protected float attackCooldown;

    protected Enemy target;

    protected Animation<TextureRegion> initAnimation;
    protected float animationTime = 0f;
    protected boolean isInitializing = true;

    public Tower(float x, float y, TowerType type, GameLoop gameLoop) {
        super(x, y);
        this.type = type;
        this.gameLoop = gameLoop;
    }

    public abstract void tryUpgrade();
    public abstract int getUpgradeCost();
    protected abstract void shoot(Array<Projectile> projectilesToSpawn);

    /**
     * Загружает горизонтальный spritesheet появления (кадры 32×32).
     */
    protected void loadInitAnimation(String assetPath) {
        Texture sheet = new Texture(Gdx.files.internal(assetPath));
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);
        int totalFrames = tmp[0].length;
        TextureRegion[] animationFrames = new TextureRegion[totalFrames];
        System.arraycopy(tmp[0], 0, animationFrames, 0, totalFrames);
        this.initAnimation = new Animation<>(0.06f, animationFrames);
    }

    public void update(float deltaTime, Array<Enemy> enemies, Array<Projectile> projectilesToSpawn) {
        if (baseDamage == 0 && damage > 0) {
            baseDamage = damage;
            baseAttackRange = attackRange;
            baseAttackCooldown = attackCooldown;
        }

        if (isInitializing) {
            animationTime += deltaTime;
            if (initAnimation != null && initAnimation.isAnimationFinished(animationTime)) {
                isInitializing = false;
            }
            return;
        }

        this.damage = baseDamage;
        this.attackRange = baseAttackRange;
        this.attackCooldown = baseAttackCooldown;

        for (Item item : gameLoop.getInventoryManager().getEquippedSlots()) {
            item.applyEffect(this);
        }

        updateCombat(deltaTime, enemies, projectilesToSpawn);
    }

    /**
     * Обычные башни ищут цель и стреляют. Особые (тесла) переопределяют поведение.
     */
    protected void updateCombat(float deltaTime, Array<Enemy> enemies, Array<Projectile> projectilesToSpawn) {
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

    /** Вызывается перед удалением башни с карты (продажа и т.п.). */
    public void onRemoved() {}

    @Override
    public void update(float deltaTime) {}

    protected void checkAndFindTarget(Array<Enemy> enemies) {
        if (target != null && target.isActive() && position.dst(target.getPosition()) <= attackRange) {
            return;
        }
        target = null;
        for (int i = 0; i < enemies.size; i++) {
            Enemy enemy = enemies.get(i);
            if (enemy.isActive() && position.dst(enemy.getPosition()) <= attackRange) {
                target = enemy;
                break;
            }
        }
    }

    public float getAttackRange() { return attackRange; }
    public float getDamage() { return damage; }
    public float getAttackCooldown() { return attackCooldown; }
    public TowerType getType() { return type; }
    public int getCurrentLevel() { return currentLevel; }

    public boolean isInitializing() { return isInitializing; }

    public TextureRegion getCurrentInitFrame() {
        return initAnimation != null ? initAnimation.getKeyFrame(animationTime) : null;
    }

    public float getDynamicRange() { return attackRange; }
    public void setDynamicRange(float r) { this.attackRange = r; }
    public float getDynamicDamage() { return damage; }
    public void setDynamicDamage(float d) { this.damage = d; }
    public float getDynamicCooldown() { return attackCooldown; }
    public void setDynamicCooldown(float c) { this.attackCooldown = c; }
}
