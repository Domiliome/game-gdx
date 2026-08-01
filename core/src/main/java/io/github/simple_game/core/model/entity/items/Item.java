package io.github.simple_game.core.model.entity.items;

import io.github.simple_game.core.model.entity.enemy.EnemyTier;
import io.github.simple_game.core.model.entity.tower.Tower;

public abstract class Item {
    protected final String name;
    protected final String description;
    protected final float dropChance;
    protected final EnemyTier requiredTier; // Проверяем, что поле финальное

    public Item(String name, String description, float dropChance, EnemyTier requiredTier) {
        this.name = name;
        this.description = description;
        this.dropChance = dropChance;
        this.requiredTier = requiredTier; // Важно: проверяем запись!
    }

    public abstract void applyEffect(Tower tower);
    public abstract Item clonePrototype();

    public String getName() { return name; }
    public String getDescription() { return description; }
    public float getDropChance() { return dropChance; }


    public EnemyTier getRequiredTier() { return requiredTier; }
}
