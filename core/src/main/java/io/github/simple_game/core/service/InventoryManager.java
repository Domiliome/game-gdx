package io.github.simple_game.core.service;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.enemy.EnemyTier;
import io.github.simple_game.core.model.entity.items.*;

public class InventoryManager {
    private final Array<Item> backpack = new Array<>();
    private final Array<Item> lootTable = new Array<>();
    private final Array<Item> equippedSlots = new Array<>(3);
    private final Array<Item> forgeSlots = new Array<>(3);

    public InventoryManager() {
        lootTable.add(new SharpArrow());
        lootTable.add(new IronCore());
        lootTable.add(new MagicCrystal());
        // Добавляем SpringArrow в общий каталог, чтобы система знала её рецепт крафта
        lootTable.add(new SpringArrow());
    }

    public void calculateLootDrop(Enemy enemy) {
        EnemyTier deadEnemyTier = enemy.getTier();
        if (deadEnemyTier == null) return;
        for (Item item : lootTable) {
            if (item.getRequiredTier() != deadEnemyTier) continue;
            if (MathUtils.random() <= item.getDropChance()) {
                backpack.add(item.clonePrototype());
                System.out.println("🎉 ПОЛУЧЕН ЛУТ: " + item.getName());
                break;
            }
        }
    }

    /**
     * Сканирует каталог предметов и ищет тот, чей рецепт совпал с ингредиентами в кузнице.
     * @return подходящий для крафта предмет-шаблон или null, если лежит хлам.
     */
    public Item getCraftResult() {
        if (forgeSlots.size < 3) return null;

        // Паттерн "Цепочка обязанностей": опрашиваем каждый предмет, подходит ли рецепт
        for (Item item : lootTable) {
            if (item.checkRecipe(forgeSlots)) {
                return item; // Нашли предмет, который узнал свои ингредиенты!
            }
        }
        return null;
    }

    /**
     * Логика полиморфной перековки. Автоматически сжигает ингредиенты и выдает результат рецепта.
     */
    public boolean forge() {
        Item resultTemplate = getCraftResult();
        if (resultTemplate == null) return false; // Защита: рецепт невалиден

        // Безвозвратно уничтожаем 3 ингредиента
        forgeSlots.clear();

        // Временно для теста: 100% шанс создания (1.00f)
        if (MathUtils.random() <= 1.00f) {
            backpack.add(resultTemplate.clonePrototype()); // Добавляем независимый клон в рюкзак
            System.out.println("🔥 УСПЕШНО СКРАФЧЕН ПРЕДМЕТ: " + resultTemplate.getName());
            return true;
        }
        return false;
    }

    public boolean equipItem(Item item) {
        if (equippedSlots.size < 3 && backpack.removeValue(item, true)) {
            equippedSlots.add(item); return true;
        }
        return false;
    }

    public void unequipItem(Item item) {
        if (equippedSlots.removeValue(item, true)) backpack.add(item);
    }

    public boolean addItemToForge(Item item) {
        if (forgeSlots.size < 3 && backpack.removeValue(item, true)) {
            forgeSlots.add(item); return true;
        }
        return false;
    }

    public void removeItemFromForge(Item item) {
        if (forgeSlots.removeValue(item, true)) backpack.add(item);
    }

    public Array<Item> getBackpack() { return backpack; }
    public Array<Item> getEquippedSlots() { return equippedSlots; }
    public Array<Item> getForgeSlots() { return forgeSlots; }
}
