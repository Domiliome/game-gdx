package io.github.simple_game.core.model;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.model.entity.tower.Tower;

/**
 * Узкий доступ к миру для башен и снарядов. Реализуется сервисом сессии, а не наоборот.
 */
public interface CombatWorld {
    Array<Tower> getTowers();
    Array<Enemy> getEnemies();
    Array<Item> getEquippedItems();
}
