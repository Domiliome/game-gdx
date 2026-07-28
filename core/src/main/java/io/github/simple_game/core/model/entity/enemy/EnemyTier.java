package io.github.simple_game.core.model.entity.enemy;

/**
 * Перечисление тиров (категорий сложности) для автоматического баланса волн.
 */
public enum EnemyTier {
    TIER_1_LIGHT(1),   // Быстрые, но хлипкие юниты (Гоблины). Занимают 1 очко бюджета.
    TIER_2_NORMAL(2),  // Классические сбалансированные юниты (Зомби). Занимают 2 очка.
    TIER_3_HEAVY(4);   // Живучие медленные танки (Орки). Занимают 4 очка бюджета волны.

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
