package io.github.simple_game.core.model.entity;

public class UpgradePath {
    private int currentLevel = 1;
    private final int maxLevel = 5;

    // Коэффициенты увеличения характеристик за каждый уровень (например, +20%)
    private final float damageMultiplier = 1.2f;
    private final float rangeMultiplier = 1.1f;
    private final float cooldownReduction = 0.95f; // Атака становится быстрее на 5%

    public UpgradePath() {
        // Конструктор по умолчанию
    }

    // Метод для прокачки башни
    public boolean upgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            return true; // Улучшение успешно
        }
        return false; // Достигнут максимальный уровень
    }

    // Стоимость улучшения (растет с каждым уровнем)
    public int getUpgradeCost(TowerType type) {
        return (int) (type.getCost() * 0.6f * currentLevel);
    }

    // Расчет текущих характеристик с учетом уровня башни
    public float getCurrentDamage(TowerType type) {
        return type.getBaseDamage() * (float) Math.pow(damageMultiplier, currentLevel - 1);
    }

    public float getCurrentRange(TowerType type) {
        return type.getBaseRange() * (float) Math.pow(rangeMultiplier, currentLevel - 1);
    }

    public float getCurrentCooldown(TowerType type) {
        // Ограничим максимальную скорость атаки, чтобы башня не стреляла бесконечно
        return Math.max(0.1f, type.getBaseCooldown() * (float) Math.pow(cooldownReduction, currentLevel - 1));
    }

    public int getCurrentLevel() { return currentLevel; }
    public boolean isMaxLevel() { return currentLevel >= maxLevel; }
}
