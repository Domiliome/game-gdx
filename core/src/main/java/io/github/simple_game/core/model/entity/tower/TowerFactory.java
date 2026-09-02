package io.github.simple_game.core.model.entity.tower;

import io.github.simple_game.core.model.CombatWorld;

/**
 * Фабрика создания экземпляра башни по типу из каталога.
 */
@FunctionalInterface
public interface TowerFactory {
    Tower create(float x, float y, CombatWorld world);
}
