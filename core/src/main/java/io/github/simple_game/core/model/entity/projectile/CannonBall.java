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
        // Помечаем текущий снаряд неактивным, чтобы удалить его из игры
        active = false;

        // Запоминаем точные координаты эпицентра взрыва
        float explosionX = position.x;
        float explosionY = position.y;


        // Получаем массив всех активных врагов, находящихся сейчас на карте
        Array<Enemy> allEnemies = gameLoop.getEnemies();

        // Проходим по врагам с конца в начало (обратный цикл) для безопасного удаления объектов при гибели
        for (int i = allEnemies.size - 1; i >= 0; i--) {
            Enemy currentEnemy = allEnemies.get(i);

            // Вычисляем расстояние от эпицентра взрыва до текущего проверяемого врага
            float distanceToExplosion = currentEnemy.getPosition().dst(explosionX, explosionY);

            // Если враг находится внутри взрывной волны — наносим урон
            if (distanceToExplosion <= BLAST_RADIUS) {
                currentEnemy.takeDamage(damage, economy);

            }
        }
    }
}
