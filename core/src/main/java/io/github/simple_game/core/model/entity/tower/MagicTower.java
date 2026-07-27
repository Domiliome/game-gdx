package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.utils.Array;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.model.entity.projectile.MagicSphere;

/**
 * Магическая башня. Стреляет энергетическими сферами.
 * Полностью контролирует свои боевые характеристики, формулы улучшений
 * и накладывает на врагов эффект замедления при попадании специализированного снаряда.
 */
public class MagicTower extends Tower {

    // Стартовые базовые параметры мага на первом уровне
    private static final float BASE_DAMAGE = 25f;
    private static final float BASE_RANGE = 180f;
    private static final float BASE_COOLDOWN = 1.0f;

    // Уникальные коэффициенты прокачки только для Магической башни
    private final float damageMultiplier = 1.3f;   // +30% урона за каждый уровень
    private final float rangeMultiplier = 1.15f;   // +15% радиуса за уровень (высокий прирост дальности)
    private final float cooldownReduction = 0.98f; // Скорость атаки почти не растет

    /**
     * Создает новую магическую башню в заданных координатах.
     *
     * @param x        координата X для установки башни на карте
     * @param y        координата Y для установки башни на карте
     * @param gameLoop актуальная ссылка на игровой цикл для передачи контекста окружения
     */
    public MagicTower(float x, float y, GameLoop gameLoop) {
        super(x, y, TowerType.MAGIC, gameLoop);
        this.damage = BASE_DAMAGE;
        this.attackRange = BASE_RANGE;
        this.attackCooldown = BASE_COOLDOWN;
    }

    /**
     * Повышает уровень магической башни до максимального предела.
     * Пересчитывает боевые характеристики (урон, радиус, скорость) по уникальным формулам мага
     * на основе внутренних констант прогрессии.
     */
    @Override
    public void tryUpgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            this.damage = BASE_DAMAGE * (float) Math.pow(damageMultiplier, currentLevel - 1);
            this.attackRange = BASE_RANGE * (float) Math.pow(rangeMultiplier, currentLevel - 1);
            this.attackCooldown = Math.max(0.1f, BASE_COOLDOWN * (float) Math.pow(cooldownReduction, currentLevel - 1));
            System.out.println("Магическая башня улучшена до уровня " + currentLevel);
        }
    }

    /**
     * Возвращает стоимость улучшения для магической башни.
     * Маг имеет повышенную стоимость апгрейда по сравнению с другими оборонительными постройками.
     *
     * @return стоимость улучшения в золотых монетах
     */
    @Override
    public int getUpgradeCost() {
        return (int) (type.getCost() * 0.8f * currentLevel);
    }

    /**
     * Производит выстрел по текущей установленной цели.
     * Создает экземпляр специализированного магического снаряда {@link MagicSphere},
     * который при столкновении замедляет вражеского юнита.
     *
     * @param projectilesToSpawn буферный список для добавления нового снаряда в игровой мир
     */
    @Override
    protected void shoot(Array<Projectile> projectilesToSpawn) {
        Projectile magicSphere = new MagicSphere(position.x, position.y, target, damage, type);
        projectilesToSpawn.add(magicSphere);
        System.out.println("Маг запустил сферу. Урон: " + damage + " (Эффект: Замедление)");
    }
}
