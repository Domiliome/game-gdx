package io.github.simple_game.core.model.entity;

import com.badlogic.gdx.utils.Array;

public class Tower extends Entity {
    private final TowerType type;
    private final UpgradePath upgradePath;

    private float damage;
    private float attackRange;
    private float attackCooldown;
    private float shootTimer = 0f;

    private Enemy target;

    public Tower(float x, float y, TowerType type) {
        super(x, y);
        this.type = type;
        this.upgradePath = new UpgradePath();
        updateSpecs();
    }

    private void updateSpecs() {
        this.damage = upgradePath.getCurrentDamage(type);
        this.attackRange = upgradePath.getCurrentRange(type);
        this.attackCooldown = upgradePath.getCurrentCooldown(type);
    }

    public void tryUpgrade() {
        if (upgradePath.upgrade()) {
            updateSpecs();
        }
    }

    // Перегружаем метод update, чтобы передавать список врагов из GameLoop
    public void update(float deltaTime, Array<Enemy> enemies, Array<Projectile> projectilesToSpawn) {
        // 1. Ищем или проверяем цель
        checkAndFindTarget(enemies);

        // 2. Если цель есть, увеличиваем таймер перезарядки
        if (target != null) {
            shootTimer += deltaTime;

            // 3. Если башня перезарядилась — стреляем
            if (shootTimer >= attackCooldown) {
                shoot(projectilesToSpawn);
                shootTimer = 0f; // Сбрасываем таймер
            }
        } else {
            // Если цели нет, плавно сбрасываем таймер (или оставляем готовым к выстрелу)
            shootTimer = attackCooldown;
        }
    }

    // В базовом методе без параметров просто сбрасываем таймер, если нужно контракту Entity
    @Override
    public void update(float deltaTime) {
        // Этот метод оставим пустым или вызовем исключение,
        // так как башне обязательно нужен список врагов.
    }

    /**
     * Логика поиска цели: выбирает ПЕРВОГО врага, который зашел в радиус атаки.
     */
    private void checkAndFindTarget(Array<Enemy> enemies) {
        // Проверяем старую цель: если она жива и все еще в радиусе, продолжаем её атаковать
        if (target != null && target.isActive() && position.dst(target.getPosition()) <= attackRange) {
            return;
        }

        // Если старая цель погибла или ушла из радиуса — ищем новую
        target = null;

        for (Enemy enemy : enemies) {
            if (enemy.isActive() && position.dst(enemy.getPosition()) <= attackRange) {
                target = enemy; // Захватываем первого подошедшего врага
                break;
            }
        }
    }

    /**
     * Создает снаряд и направляет его в сторону цели
     */
    private void shoot(Array<Projectile> projectilesToSpawn) {
        // Создаем снаряд в центре башни и передаем ему текущую цель и урон
        Projectile projectile = new Projectile(position.x, position.y, target, damage, type);
        projectilesToSpawn.add(projectile);
    }

    // Геттеры для отрисовки радиуса на экране (полезно при дебаге)
    public float getAttackRange() { return attackRange; }
    public TowerType getType() { return type; }
}
