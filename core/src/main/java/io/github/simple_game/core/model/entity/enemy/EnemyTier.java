package io.github.simple_game.core.model.entity.enemy;

/**
 * Перечисление тиров (категорий сложности) для автоматического баланса волн.
 */
public enum EnemyTier {
    TIER_1_LIGHT(1),
    TIER_2_NORMAL(2),
    TIER_3_HEAVY(4);

    private final int weight;

    EnemyTier(int weight) {
        this.weight = weight;
    }

    /**
     * @return сколько "очков сложности" из бюджета волны забирает один такой враг
     */
    public int getWeight() {
        return weight;
    }
}
