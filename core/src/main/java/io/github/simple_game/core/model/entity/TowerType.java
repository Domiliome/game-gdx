package io.github.simple_game.core.model.entity;

/**
 * Перечисление, определяющее доступные типы оборонительных башен в игре.
 * Каждая константа хранит уникальный набор стартовых характеристик,
 * включая урон, дальнобойность, скорость атаки и стоимость постройки.
 */
public enum TowerType {
    /**
     * Башня лучников. Обладает высокой скоростью атаки и средней дальностью,
     * наносит небольшой базовый урон. Эффективна против быстрых одиночных целей.
     */
    ARCHER(15f, 150f, 0.8f, 100),

    /**
     * Артиллерийская пушка. Наносит огромный урон с низкой скоростью атаки
     * и укороченным радиусом действия. Идеальна против медленных бронированных врагов.
     */
    CANNON(40f, 120f, 2.0f, 250),

    /**
     * Магическая башня. Обладает максимальным радиусом поражения на карте
     * и сбалансированными боевыми показателями урона и скорости перезарядки.
     */
    MAGIC(25f, 180f, 1.2f, 200);

    private final float baseDamage;
    private final float baseRange;
    private final float baseCooldown;
    private final int cost;

    /**
     * Инициализирует конфигурацию конкретного типа оборонительного сооружения.
     *
     * @param baseDamage   базовый урон, наносимый одним снарядом башни
     * @param baseRange    стартовый радиус автоматического обнаружения и атаки целей
     * @param baseCooldown время перезарядки орудия между выстрелами в секундах
     * @param cost         стоимость покупки и возведения башни на карте
     */
    TowerType(float baseDamage, float baseRange, float baseCooldown, int cost) {
        this.baseDamage = baseDamage;
        this.baseRange = baseRange;
        this.baseCooldown = baseCooldown;
        this.cost = cost;
    }

    /**
     * @return начальное значение наносимого урона для данного типа башни
     */
    public float getBaseDamage() { return baseDamage; }

    /**
     * @return стартовый радиус (дальнобойность) атаки башни в пикселях
     */
    public float getBaseRange() { return baseRange; }

    /**
     * @return базовое время перезарядки между выстрелами в секундах
     */
    public float getBaseCooldown() { return baseCooldown; }

    /**
     * @return количество золота, необходимое для покупки и постройки башни
     */
    public int getCost() { return cost; }
}
