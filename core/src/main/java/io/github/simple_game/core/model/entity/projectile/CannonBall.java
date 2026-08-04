package io.github.simple_game.core.model.entity.projectile;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.tower.TowerType;
import io.github.simple_game.core.service.CurrencyManager;
import io.github.simple_game.core.service.GameLoop;

/**
 * Специализированный артиллерийский снаряд (ядро), наносящий
 * взрывной урон по площади (Splash Damage) всем врагам в определенном радиусе поражения.
 */
public class CannonBall extends Projectile {
    private final GameLoop gameLoop;

    // Радиус взрывной волны ядра в пикселях (примерно чуть больше одной клетки нашей сетки 64x64)
    private static final float BLAST_RADIUS = 70f;

    /**
     * Создает новое артиллерийское ядро с привязкой к игровому циклу.
     *
     * @param x          начальная координата X появления ядра
     * @param y          начальная координата Y появления ядра
     * @param target     основной вражеский юнит, выступающий целью
     * @param damage     количество базового урона взрыва
     * @param towerType  тип башни, выпустившей снаряд
     * @param gameLoop   актуальная ссылка на игровой цикл для получения списка всех врагов на карте
     */
    public CannonBall(float x, float y, Enemy target, float damage, TowerType towerType, GameLoop gameLoop) {
        super(x, y, target, damage, towerType);
        this.gameLoop = gameLoop;
    }

    /**
     * Обрабатывает успешное столкновение ядра с целью.
     * Деактивирует снаряд, фиксирует точку взрыва и наносит урон всем активным
     * врагам, оказавшимся в радиусе действия взрывной волны.
     *
     * @param economy ссылка на менеджер экономики для начисления наград за возможные убийства врагов
     */
    @Override
    protected void hitTarget(CurrencyManager economy) {
        active = false;
        float explosionX = position.x;
        float explosionY = position.y;
        Array<Enemy> allEnemies = gameLoop.getEnemies();
        for (int i = allEnemies.size - 1; i >= 0; i--) {
            Enemy currentEnemy = allEnemies.get(i);
            float distanceToExplosion = currentEnemy.getPosition().dst(explosionX, explosionY);
            if (distanceToExplosion <= BLAST_RADIUS) {
                currentEnemy.takeDamage(damage, economy);

            }
        }
    }
}
