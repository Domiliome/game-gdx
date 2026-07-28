package io.github.simple_game.core.model.entity.items;

import io.github.simple_game.core.model.entity.enemy.EnemyTier;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.entity.tower.TowerType;

public class IronCore extends Item {
    public IronCore() {
        super("Heavy Core", "Cannon damage +25", 0.10f, EnemyTier.TIER_3_HEAVY);
    }

    @Override
    public void applyEffect(Tower tower) {
        if (tower.getType() == TowerType.CANNON) {
            tower.setDamage(tower.getDamage() + 25f);
        }
    }
}
