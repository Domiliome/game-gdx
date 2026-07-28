package io.github.simple_game.core.model.entity.items;

import io.github.simple_game.core.model.entity.enemy.EnemyTier;
import io.github.simple_game.core.model.entity.tower.Tower;

/**
 * Абстрактный базовый класс для всех предметов в игре.
 * Сочетает в себе свойства предмета и контракт стратегии его применения.
 */
public abstract class Item {
    protected final String name;
    protected final String description;
    protected final float dropChance;
    protected final EnemyTier requiredTier; // С какого тира врагов выпадает

    public Item(String name, String description, float dropChance, EnemyTier requiredTier) {
        this.name = name;
        this.description = description;
        this.dropChance = dropChance;
        this.requiredTier = requiredTier;
    }

    /**
     * Абстрактный метод стратегии. Каждый конкретный класс предмета
     * реализует свою формулу модификации характеристик башни.
     */
    public abstract void applyEffect(Tower tower);

    public String getName() { return name; }
    public String getDescription() { return description; }
    public float getDropChance() { return dropChance; }
    public EnemyTier getRequiredTier() { return requiredTier; }
}
