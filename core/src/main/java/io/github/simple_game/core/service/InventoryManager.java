package io.github.simple_game.core.service;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.enemy.EnemyTier;
import io.github.simple_game.core.model.entity.items.IronCore;
import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.model.entity.items.MagicCrystal;
import io.github.simple_game.core.model.entity.items.SharpArrow;

public class InventoryManager {
    private final Array<Item> backpack = new Array<>();
    private final Array<Item> lootTable = new Array<>();

    private final Array<Item> equippedSlots = new Array<>(3);

    public InventoryManager() {
        lootTable.add(new SharpArrow());
        lootTable.add(new IronCore());
        lootTable.add(new MagicCrystal());
    }

    public void calculateLootDrop(Enemy enemy) {
        EnemyTier deadEnemyTier = enemy.getTier();

        for (Item item : lootTable) {
            if (item.getRequiredTier() != null && item.getRequiredTier() != deadEnemyTier) {
                continue;
            }
            if (MathUtils.random() <= item.getDropChance()) {
                backpack.add(item.clonePrototype());
                System.out.println("take loot : " + item.getName() + " from enemy tier " + deadEnemyTier);
                break;
            }
        }
    }



    public boolean equipItem(Item item) {
        if (equippedSlots.size < 3 && backpack.removeValue(item, true)) {
            equippedSlots.add(item);
            return true;
        }
        return false;
    }


    public void unequipItem(Item item) {
        if (equippedSlots.removeValue(item, true)) {
            backpack.add(item);
        }
    }

    public Array<Item> getBackpack() { return backpack; }
    public Array<Item> getEquippedSlots() { return equippedSlots; } // Геттер гнёзд

    public void clear() {
        backpack.clear();
        equippedSlots.clear();
    }
}
