package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;
import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.Projectile;
import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.model.movement.RoadPath;

/**
 * Ядро игрового процесса (Центральный игровой цикл).
 * Отвечает за хранение списков всех сущностей на карте, координацию их
 * логического обновления каждый кадр и передачу контекста данных между менеджерами.
 */
public class GameLoop {
    private final Array<Enemy> enemies;
    private final Array<Tower> towers;
    private final Array<Projectile> projectiles;
    private final Array<Projectile> projectilesToSpawn;

    private RoadPath roadPath;
    private final WaveManager waveManager;
    private final CurrencyManager currencyManager;

    /**
     * Создает новый игровой цикл. Инициализирует списки сущностей,
     * строит дефолтный маршрут движения для уровня, запускает менеджер волн
     * и подсистему игровой экономики.
     */
    public GameLoop() {
        this.enemies = new Array<>();
        this.towers = new Array<>();
        this.projectiles = new Array<>();
        this.projectilesToSpawn = new Array<>();

        initLevelPath();
        this.waveManager = new WaveManager(roadPath);
        this.currencyManager = new CurrencyManager(250, 20);
    }

    /**
     * Внутренний метод для формирования статических точек пути (вейпоинтов),
     * по которым наземные враги будут перемещаться от точки спавна до базы игрока.
     */
    private void initLevelPath() {
        roadPath = new RoadPath();
        roadPath.addPoint(240, 800);
        roadPath.addPoint(240, 500);
        roadPath.addPoint(64, 500);
        roadPath.addPoint(64, 200);
        roadPath.addPoint(416, 200);
        roadPath.addPoint(416, 0);
    }

    /**
     * Главный метод такта игры. Непрерывно вызывается из игрового экрана.
     * Последовательно запускает логику спавна волн, передвижения врагов, ИИ башен и полета снарядов.
     * Передает контекст экономики (CurrencyManager) сущностям для начисления наград или списания жизней.
     * Использует обратные циклы для безопасного удаления уничтоженных сущностей во время итерации.
     *
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     */
    public void update(float deltaTime) {
        waveManager.update(deltaTime, enemies);

        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(deltaTime, currencyManager);

            if (!enemy.isActive()) {
                enemies.removeIndex(i);
            }
        }

        projectilesToSpawn.clear();
        for (Tower tower : towers) {
            tower.update(deltaTime, enemies, projectilesToSpawn);
        }

        projectiles.addAll(projectilesToSpawn);

        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update(deltaTime, currencyManager);

            if (!projectile.isActive()) {
                projectiles.removeIndex(i);
            }
        }
    }

    /**
     * Регистрирует новую построенную башню в списках игрового мира.
     *
     * @param tower созданный экземпляр оборонительной башни
     */
    public void addTower(Tower tower) {
        towers.add(tower);
    }

    /**
     * @return динамический список всех живых врагов, находящихся на карте
     */
    public Array<Enemy> getEnemies() { return enemies; }

    /**
     * @return список всех возведенных игроком башен
     */
    public Array<Tower> getTowers() { return towers; }

    /**
     * @return список летящих к целям снарядов
     */
    public Array<Projectile> getProjectiles() { return projectiles; }

    /**
     * @return текущий настроенный маршрут движения для врагов
     */
    public RoadPath getRoadPath() { return roadPath; }

    /**
     * @return ссылку на активный менеджер волн наступающих мобов
     */
    public WaveManager getWaveManager() { return waveManager; }

    /**
     * @return ссылку на менеджер экономики и внутриигрового баланса игрока
     */
    public CurrencyManager getCurrencyManager() { return currencyManager; }
}
