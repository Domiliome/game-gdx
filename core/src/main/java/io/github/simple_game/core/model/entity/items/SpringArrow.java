package io.github.simple_game.core.model.entity.items;

import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.entity.tower.TowerType;

/**
 * Уникальный артефакт "Пружинная стрела".
 * Не выпадает с монстров. Повышает скорость атаки лучников на 25%.
 */
public class SpringArrow extends Item {

    public SpringArrow() {

        super("Spring Arrow", "Archer attack cooldown -25%", 0.0f, null);
    }

    @Override
    public boolean checkRecipe(com.badlogic.gdx.utils.Array<Item> forgeSlots) {

        if (forgeSlots.size < 3) return false;


        for (Item ingredient : forgeSlots) {
            if (!(ingredient instanceof SharpArrow)) {
                return false;
            }
        }
        return true; // Рецепт идеален!
    }

    @Override
    public void applyEffect(Tower tower) {
        if (tower.getType() == TowerType.ARCHER) {
            tower.setDynamicCooldown(tower.getDynamicCooldown() * 0.75f);
        }
    }

    @Override
    public Item clonePrototype() {
        return new SpringArrow();
    }
}
