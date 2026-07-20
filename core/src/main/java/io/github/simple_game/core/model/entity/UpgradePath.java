package io.github.simple_game.core.model.entity;

/**
 * Класс, управляющий прогрессией и веткой улучшений для конкретной оборонительной башни.
 * Хранит текущий уровень башни, лимиты прокачки и рассчитывает динамические
 * боевые характеристики (урон, радиус, перезарядку) на основе математических коэффициентов.
 */
public class UpgradePath {
    private int currentLevel = 1;
    private final int maxLevel = 5;

    private final float damageMultiplier = 1.2f;
    private final float rangeMultiplier = 1.1f;
    private final float cooldownReduction = 0.95f;

    /**
     * Создает новую ветку улучшений со стартовым уровнем, равным 1.
     */
    public UpgradePath() {
        // Конструктор по умолчанию
    }

    /**
     * Повышает текущий уровень башни на единицу, если максимальный уровень еще не достигнут.
     *
     * @return true, если уровень успешно повышен; false, если достигнут максимальный предел улучшений
     */
    public boolean upgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            return true; // Улучшение успешно
        }
        return false; // Достигнут максимальный уровень
    }

    /**
     * Рассчитывает стоимость следующего улучшения башни.
     * Стоимость прогрессивно увеличивается с каждым текущим уровнем сооружения.
     *
     * @param type базовый тип башни, для которой вычисляется цена апгрейда
     * @return стоимость улучшения в золотых монетах
     */
    public int getUpgradeCost(TowerType type) {
        return (int) (type.getCost() * 0.6f * currentLevel);
    }

    /**
     * Рассчитывает текущий урон башни с учетом повышающих коэффициентов уровня.
     *
     * @param type базовый тип башни
     * @return модифицированное значение наносимого урона
     */
    public float getCurrentDamage(TowerType type) {
        return type.getBaseDamage() * (float) Math.pow(damageMultiplier, currentLevel - 1);
    }

    /**
     * Рассчитывает текущий радиус атаки башни с учетом коэффициентов дальнобойности.
     *
     * @param type базовый тип башни
     * @return модифицированное значение радиуса поражения в пикселях
     */
    public float getCurrentRange(TowerType type) {
        return type.getBaseRange() * (float) Math.pow(rangeMultiplier, currentLevel - 1);
    }

    /**
     * Рассчитывает текущее время перезарядки орудия башни.
     * Скорость атаки увеличивается с каждым уровнем, при этом вводится безопасное ограничение
     * минимального кулдауна, чтобы предотвратить бесконечную мгновенную стрельбу.
     *
     * @param type базовый тип башни
     * @return модифицированное время перезарядки между выстрелами в секундах
     */
    public float getCurrentCooldown(TowerType type) {
        return Math.max(0.1f, type.getBaseCooldown() * (float) Math.pow(cooldownReduction, currentLevel - 1));
    }

    /**
     * @return текущий уровень прокачки башни
     */
    public int getCurrentLevel() { return currentLevel; }

    /**
     * @return true, если башня достигла финального уровня и дальнейший апгрейд невозможен; иначе false
     */
    public boolean isMaxLevel() { return currentLevel >= maxLevel; }
}
