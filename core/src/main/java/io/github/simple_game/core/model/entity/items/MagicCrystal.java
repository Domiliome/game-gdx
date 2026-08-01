package io.github.simple_game.core.model.entity.items;

import io.github.simple_game.core.model.entity.enemy.EnemyTier;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.entity.tower.TowerType;

public class MagicCrystal extends Item {
    public MagicCrystal() {
        super("Mana Crystal", "Magic cooldown -20%", 0.08f,EnemyTier.TIER_2_NORMAL);
    }
    @Override
    public Item clonePrototype() { return new MagicCrystal(); }

    @Override
    public void applyEffect(Tower tower) {
        if (tower.getType() == TowerType.MAGIC) {
            tower.setDynamicCooldown(tower.getDynamicCooldown() * 0.8f);
        }
    }
}
