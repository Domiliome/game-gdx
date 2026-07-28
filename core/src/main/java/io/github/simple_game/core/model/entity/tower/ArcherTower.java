package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.projectile.Arrow;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;
/**
 * Башня лучников. Обладает высокой скоростью атаки и сбалансированным уроном.
 * Полностью контролирует свои боевые характеристики, формулы улучшений
 * и рассчитывает шанс нанесения критического удара при выстреле.
 */
public class ArcherTower extends Tower {

    // Стартовые базовые параметры лучника на первом уровне
    private static final float BASE_DAMAGE = 15f;
    private static final float BASE_RANGE = 150f;
    private static final float BASE_COOLDOWN = 0.6f;

    // Уникальные коэффициенты прокачки только для Башни лучников
    private final float damageMultiplier = 1.15f;  // +15% урона за каждый уровень
    private final float rangeMultiplier = 1.05f;   // +5% радиуса за уровень (небольшой прирост дальности)
    private final float cooldownReduction = 0.90f; // Сильное ускорение атаки (-10% кулдауна за уровень)

    // Параметры механики критического удара
    private float critChance = 0.20f;                // Стартовый шанс критического удара (20%)
    private final float critDamageMultiplier = 2.0f; // Множитель критического урона (x2)

    /**
     * Создает новую башню лучников в заданных координатах.
     *
     * @param x        координата X для установки башни на карте
     * @param y        координата Y для установки башни на карте
     * @param gameLoop актуальная ссылка на игровой цикл для передачи контекста окружения
     */
    public ArcherTower(float x, float y, GameLoop gameLoop) {
        super(x, y, TowerType.ARCHER, gameLoop);
        this.damage = BASE_DAMAGE;
        this.attackRange = BASE_RANGE;
        this.attackCooldown = BASE_COOLDOWN;
    }

    /**
     * Повышает уровень башни лучников до максимального предела.
     * Пересчитывает боевые характеристики по уникальным формулам скорострельности,
     * а также плавно увеличивает шанс нанесения критического удара на 5% за уровень.
     */
    @Override
    public void tryUpgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            this.damage = BASE_DAMAGE * (float) Math.pow(damageMultiplier, currentLevel - 1);
            this.attackRange = BASE_RANGE * (float) Math.pow(rangeMultiplier, currentLevel - 1);
            this.attackCooldown = Math.max(0.1f, BASE_COOLDOWN * (float) Math.pow(cooldownReduction, currentLevel - 1));

            // Шанс критического удара растет с каждым уровнем (20% -> 25% -> 30% -> 35% -> 40%)
            this.critChance = 0.20f + (currentLevel - 1) * 0.05f;

            System.out.println("Башня лучников улучшена до уровня " + currentLevel + "! Шанс крита: " + (critChance * 100) + "%");
        }
    }

    /**
     * Возвращает стоимость улучшения для башни лучников.
     * Лучник имеет самую бюджетную стоимость апгрейда среди всех защитных сооружений.
     *
     * @return стоимость улучшения в золотых монетах
     */
    @Override
    public int getUpgradeCost() {
        return (int) (type.getCost() * 0.5f * currentLevel);
    }

    /**
     * Производит выстрел по текущей установленной цели.
     * Рассчитывает вероятность критического удара. В случае успеха наносит цели
     * двойной урон и генерирует специализированный снаряд {@link Arrow} с флагом крита.
     *
     * @param projectilesToSpawn буферный список для добавления нового снаряда в игровой мир
     */
    @Override
    protected void shoot(Array<Projectile> projectilesToSpawn) {
        // Бросаем случайное значение от 0.0 до 1.0 для проверки вероятности крита
        boolean isCrit = Math.random() < critChance;

        // Накладываем множитель урона, если крит сработал
        float finalDamage = isCrit ? this.damage * critDamageMultiplier : this.damage;

        // Создаем специализированный снаряд стрелы
        Projectile arrow = new Arrow(position.x, position.y, target, finalDamage, type, isCrit);
        projectilesToSpawn.add(arrow);

        if (isCrit) {
            System.out.println("🔥 КРИТИЧЕСКИЙ ВЫСТРЕЛ! Стрела нанесет: " + finalDamage + " ед. урона");
        } else {
            System.out.println("Лучник выпустил стрелу с уроном " + finalDamage);
        }
    }
}
