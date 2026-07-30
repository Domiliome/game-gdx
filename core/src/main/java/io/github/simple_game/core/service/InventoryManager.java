package io.github.simple_game.core.service;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.enemy.EnemyTier;
import io.github.simple_game.core.model.entity.items.IronCore;
import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.model.entity.items.MagicCrystal;
import io.github.simple_game.core.model.entity.items.SharpArrow;

/**
 * Сервис управления инвентарем. Хранит массив выбитых трофеев
 * и рассчитывает вероятность выпадения лута на основе полиморфных классов предметов.
 */
public class InventoryManager {
    private final Array<Item> backpack = new Array<>();

    private final Array<Item> lootTable = new Array<>();

    public InventoryManager() {
        lootTable.add(new SharpArrow());
        lootTable.add(new IronCore());
        lootTable.add(new MagicCrystal());
    }

    /**
     * Вызывается автоматически при смерти врага. Проверяет шансы выпадения
     * артефактов, соответствующих тиру погибшего монстра.
     *
     * @param enemy погибший враг, с которого рассчитывается дроп
     */
    public void calculateLootDrop(Enemy enemy) {
        EnemyTier deadEnemyTier = enemy.getTier();

        for (Item item : lootTable) {

            if (item.getRequiredTier() != null && item.getRequiredTier() != deadEnemyTier) {
                continue;
            }

            if (MathUtils.random() <= item.getDropChance()) {
                backpack.add(item);
                System.out.println("🎉 ПРЕДМЕТ ВЫПАЛ: " + item.getName() + " (" + item.getDescription() + ")!");
                break;
            }
        }
    }

    public Array<Item> getBackpack() { return backpack; }
    public void clear() { backpack.clear(); }
}
