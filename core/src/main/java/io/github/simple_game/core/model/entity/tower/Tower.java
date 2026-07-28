package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.Entity;
import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;
/**
 * Абстрактный базовый класс для всех оборонительных башен в игре.
 * Хранит общие пространственные данные, состояние цели, таймеры перезарядки,
 * текущие боевые характеристики и ссылку на игровой цикл, делегируя расчет
 * параметров и уникальное поведение конкретным подклассам башен.
 */
public abstract class Tower extends Entity {
    protected final TowerType type;
    protected final GameLoop gameLoop;
    protected int currentLevel = 1;
    protected int maxLevel = 5;

    protected float damage;
    protected float attackRange;
    protected float attackCooldown;
    protected float shootTimer = 0f;

    protected Enemy target;

    /**
     * Конструктор для инициализации базовых параметров башни и привязки игрового контекста.
     *
     * @param x        координата X для установки башни на карте
     * @param y        координата Y для установки башни на карте
     * @param type     базовый тип башни (идентификатор для магазина)
     * @param gameLoop актуальная ссылка на игровой цикл для передачи контекста снарядам
     */
    public Tower(float x, float y, TowerType type, GameLoop gameLoop) {
        super(x, y);
        this.type = type;
        this.gameLoop = gameLoop;
    }

    /**
     * Абстрактный метод для применения апгрейда.
     * Каждый подкласс сам реализует формулу цены и увеличения характеристик.
     */
    public abstract void tryUpgrade();

    /**
     * Абстрактный метод получения стоимости улучшения.
     *
     * @return стоимость улучшения в золотых монетах
     */
    public abstract int getUpgradeCost();

    /**
     * Перегруженный метод обновления состояния башни с передачей списков контекста.
     * Вызывается каждый кадр из игрового цикла. Проверяет наличие целей и выполняет выстрел
     * с последующим сбросом таймера кулдауна.
     *
     * @param deltaTime          время, прошедшее с предыдущего кадра в секундах
     * @param enemies            список всех активных врагов на карте для поиска потенциальной цели
     * @param projectilesToSpawn буферный список игрового цикла для регистрации созданных снарядов
     */
    public void update(float deltaTime, Array<Enemy> enemies, Array<Projectile> projectilesToSpawn) {
        checkAndFindTarget(enemies);

        if (target != null) {
            shootTimer += deltaTime;

            if (shootTimer >= attackCooldown) {
                shoot(projectilesToSpawn);
                shootTimer = 0f;
            }
        } else {
            shootTimer = attackCooldown;
        }
    }

    /**
     * Базовый метод обновления без параметров.
     * Оставлен пустым в соответствии с контрактом базового класса {@link Entity},
     * так как логика башни требует обязательной передачи контекста окружения.
     *
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     */
    @Override
    public void update(float deltaTime) {
        // Оставлен пустым, так как для логики башни необходим вызов перегруженного метода update
    }

    /**
     * Осуществляет поиск и валидацию текущей цели башни.
     * Если старая цель жива и не вышла за пределы радиуса атаки, она сохраняется.
     * В противном случае башня захватывает первого активного врага из списка, вошедшего в зону поражения.
     *
     * @param enemies список всех врагов для сканирования местности
     */
    protected void checkAndFindTarget(Array<Enemy> enemies) {
        if (target != null && target.isActive() && position.dst(target.getPosition()) <= attackRange) {
            return;
        }

        target = null;

        for (Enemy enemy : enemies) {
            if (enemy.isActive() && position.dst(enemy.getPosition()) <= attackRange) {
                target = enemy;
                break;
            }
        }
    }

    /**
     * Абстрактный метод стрельбы. Каждая конкретная башня должна реализовать
     * этот метод и спавнить свой уникальный снаряд или применять специализированные эффекты.
     *
     * @param projectilesToSpawn буферный список для добавления нового снаряда в игровой мир
     */
    protected abstract void shoot(Array<Projectile> projectilesToSpawn);

    /**
     * @return текущий радиус атаки (дальнобойность) башни в пикселях
     */
    public float getAttackRange() { return attackRange; }

    /**
     * @return базовый тип этой башни
     */
    public TowerType getType() { return type; }

    /**
     * @return текущий уровень прокачки башни
     */
    public int getCurrentLevel() { return currentLevel; }
}
