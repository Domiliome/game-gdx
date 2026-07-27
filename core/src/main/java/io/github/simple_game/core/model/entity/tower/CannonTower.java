package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.utils.Array;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.model.entity.projectile.CannonBall;

/**
 * Артиллерийская пушка. Обладает сокрушительным разовым уроном.
 * Полностью контролирует свои боевые характеристики, логику улучшений
 * и выпускает тяжелые ядра, наносящие взрывной урон по площади.
 */
public class CannonTower extends Tower {

    // Стартовые базовые параметры пушки на первом уровне
    private static final float BASE_DAMAGE = 50f;
    private static final float BASE_RANGE = 120f;
    private static final float BASE_COOLDOWN = 2.0f;

    // Уникальные коэффициенты прокачки только для Артиллерийской пушки
    private final float damageMultiplier = 1.45f;   // Огромный прирост урона (+45% за каждый уровень)
    private final float rangeMultiplier = 1.08f;    // Небольшой прирост дальности
    private final float cooldownReduction = 1.0f;   // Скорость атаки не растет вообще

    /**
     * Создает новую артиллерийскую башню в заданных координатах.
     *
     * @param x        координата X для установки башни на карте
     * @param y        координата Y для установки башни на карте
     * @param gameLoop актуальная ссылка на игровой цикл для передачи контекста снарядам
     */
    public CannonTower(float x, float y, GameLoop gameLoop) {
        super(x, y, TowerType.CANNON, gameLoop);
        this.damage = BASE_DAMAGE;
        this.attackRange = BASE_RANGE;
        this.attackCooldown = BASE_COOLDOWN;
    }

    /**
     * Повышает уровень артиллерийской башни до максимального предела.
     * Пересчитывает боевые характеристики (урон и радиус) на основе локальных
     * констант прогрессии.
     */
    @Override
    public void tryUpgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            this.damage = BASE_DAMAGE * (float) Math.pow(damageMultiplier, currentLevel - 1);
            this.attackRange = BASE_RANGE * (float) Math.pow(rangeMultiplier, currentLevel - 1);
            this.attackCooldown = Math.max(0.1f, BASE_COOLDOWN * (float) Math.pow(cooldownReduction, currentLevel - 1));
            System.out.println("Пушка улучшена до уровня " + currentLevel);
        }
    }

    /**
     * Возвращает стоимость улучшения для артиллерийской башни.
     *
     * @return стоимость улучшения в золотых монетах
     */
    @Override
    public int getUpgradeCost() {
        return (int) (type.getCost() * 0.7f * currentLevel);
    }

    /**
     * Производит выстрел по текущей установленной цели.
     * Создает экземпляр специализированного артиллерийского снаряда {@link CannonBall},
     * который при столкновении наносит урон всем целям в радиусе взрыва.
     *
     * @param projectilesToSpawn буферный список для добавления нового снаряда в игровой мир
     */
    @Override
    protected void shoot(Array<Projectile> projectilesToSpawn) {
        // Спавним специализированное тяжелое ядро пушки, передавая в него игровой цикл
        Projectile cannonBall = new CannonBall(position.x, position.y, target, damage, type, gameLoop);
        projectilesToSpawn.add(cannonBall);
        System.out.println("Пушка бабахнула! Нанесено " + damage + " ед. урона по площади");
    }
}
