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

    /**
     * Создает новый игровой цикл. Инициализирует списки сущностей,
     * строит дефолтный маршрут движения для уровня и запускает менеджер волн.
     */
    public GameLoop() {
        this.enemies = new Array<>();
        this.towers = new Array<>();
        this.projectiles = new Array<>();
        this.projectilesToSpawn = new Array<>();

        initLevelPath();
        this.waveManager = new WaveManager(roadPath);
    }

    /**
     * Внутренний метод для формирования статических точек пути (вейпоинтов),
     * по которым наземные враги будут перемещаться от точки спавна до базы игрока.
     */
    private void initLevelPath() {
        roadPath = new RoadPath();
        roadPath.addPoint(240, 800);   // Старт: Верх-центр экрана (вне видимости)
        roadPath.addPoint(240, 500);   // Идут вниз до центра
        roadPath.addPoint(64, 500);    // Поворот налево
        roadPath.addPoint(64, 200);    // Поворот вниз вдоль левого края
        roadPath.addPoint(416, 200);   // Поворот направо
        roadPath.addPoint(416, 0);     // Финиш: База игрока в самом низу экрана
    }

    /**
     * Главный метод такта игры. Непрерывно вызывается из игрового экрана.
     * Последовательно запускает логику спавна волн, передвижения врагов, ИИ башен и полета снарядов.
     * Использует обратные циклы для безопасного удаления уничтоженных сущностей во время итерации.
     *
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     */
    public void update(float deltaTime) {
        waveManager.update(deltaTime, enemies);

        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(deltaTime);

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
            projectile.update(deltaTime);

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
}
