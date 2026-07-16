package io.github.simple_game.core.model.entity;

public enum TowerType {
    // Название(Урон, Радиус атаки, Перезарядка в сек, Стоимость)
    ARCHER(15f, 150f, 0.8f, 100),
    CANNON(40f, 120f, 2.0f, 250),
    MAGIC(25f, 180f, 1.2f, 200);

    private final float baseDamage;
    private final float baseRange;
    private final float baseCooldown;
    private final int cost;

    TowerType(float baseDamage, float baseRange, float baseCooldown, int cost) {
        this.baseDamage = baseDamage;
        this.baseRange = baseRange;
        this.baseCooldown = baseCooldown;
        this.cost = cost;
    }

    // Геттеры для получения стартовых параметров
    public float getBaseDamage() { return baseDamage; }
    public float getBaseRange() { return baseRange; }
    public float getBaseCooldown() { return baseCooldown; }
    public int getCost() { return cost; }
}
