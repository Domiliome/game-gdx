package io.github.simple_game.core.model.entity.items;

import io.github.simple_game.core.model.entity.enemy.EnemyTier;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.entity.tower.TowerType;

public class SharpArrow extends Item {
    public SharpArrow() {
        super("Rusty Arrow", "Archer range +40", 0.15f, EnemyTier.TIER_1_LIGHT);
    }

    @Override
    public void applyEffect(Tower tower) {
        if (tower.getType() == TowerType.ARCHER) {
            tower.setAttackRange(tower.getAttackRange() + 40f);
        }
    }
}
