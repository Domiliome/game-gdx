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
    // Добавьте этот метод внутрь абстрактного класса Item.java:
    /**
     * Проверяет, подходит ли текущий состав ингредиентов в кузнице для создания этого предмета.
     * @param forgeSlots массив из 3-х предметов, лежащих в ячейках перековки
     * @return true, если рецепт совпадает; false в противном случае
     */
    public boolean checkRecipe(com.badlogic.gdx.utils.Array<Item> forgeSlots) {
        return false; // По умолчанию обычные шмотки скрафтить нельзя
    }

    public abstract void applyEffect(Tower tower);
    public abstract Item clonePrototype();

    public String getName() { return name; }
    public String getDescription() { return description; }
    public float getDropChance() { return dropChance; }

    /**
     * Путь к PNG-иконке в {@code assets/items/}. Можно заменить своим файлом с тем же именем.
     */
    public String getIconPath() {
        return "items/" + name.replace(' ', '_').toLowerCase() + ".png";
    }


    public EnemyTier getRequiredTier() { return requiredTier; }
}
