package io.github.simple_game.core.model.entity;

import com.badlogic.gdx.utils.Array;

/**
 * Класс, представляющий оборонительную башню в игровом мире.
 * Отвечает за сканирование карты в радиусе поражения, выбор приоритетной цели,
 * отсчет времени перезарядки и генерацию боевых снарядов для атаки врагов.
 */
public class Tower extends Entity {
    private final TowerType type;
    private final UpgradePath upgradePath;

    private float damage;
    private float attackRange;
    private float attackCooldown;
    private float shootTimer = 0f;

    private Enemy target;

    /**
     * Создает новую оборонительную башню в заданных координатах.
     * Автоматически инициализирует ветку улучшений и рассчитывает базовые характеристики.
     *
     * @param x    координата X для установки башни на карте
     * @param y    координата Y для установки башни на карте
     * @param type базовый тип башни (определяет начальный урон, скорость и радиус)
     */
    public Tower(float x, float y, TowerType type) {
        super(x, y);
        this.type = type;
        this.upgradePath = new UpgradePath();
        updateSpecs();
    }

    /**
     * Пересчитывает текущие боевые характеристики башни (урон, радиус, перезарядку)
     * на основе её типа и текущего уровня улучшений из ветки апгрейдов.
     */
    private void updateSpecs() {
        this.damage = upgradePath.getCurrentDamage(type);
        this.attackRange = upgradePath.getCurrentRange(type);
        this.attackCooldown = upgradePath.getCurrentCooldown(type);
    }

    /**
     * Покушается на улучшение башни до следующего уровня.
     * Если максимальный уровень еще не достигнут, повышает его и обновляет текущие характеристики.
     */
    public void tryUpgrade() {
        if (upgradePath.upgrade()) {
            updateSpecs();
        }
    }

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
    private void checkAndFindTarget(Array<Enemy> enemies) {
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
     * Производит выстрел по текущей установленной цели.
     * Создает новый экземпляр самонаводящегося снаряда в координатах башни и передает его в игровой цикл.
     *
     * @param projectilesToSpawn буферный список для добавления нового снаряда в игровой мир
     */
    private void shoot(Array<Projectile> projectilesToSpawn) {
        Projectile projectile = new Projectile(position.x, position.y, target, damage, type);
        projectilesToSpawn.add(projectile);
    }

    /**
     * @return текущий радиус атаки (дальнобойность) башни в пикселях
     */
    public float getAttackRange() { return attackRange; }

    /**
     * @return базовый тип этой башни
     */
    public TowerType getType() { return type; }
}
