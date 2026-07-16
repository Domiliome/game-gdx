package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.Projectile;
import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.model.movement.WalkMovement;

public class GameLoop {
    // Списки всех активных игровых сущностей
    private final Array<Enemy> enemies;
    private final Array<Tower> towers;
    private final Array<Projectile> projectiles;

    // Временный буфер для снарядов, которые башни создают во время кадра
    private final Array<Projectile> projectilesToSpawn;

    // Ссылка на маршрут карты (потребуется для спавна новых врагов)
    private RoadPath roadPath;
    private float spawnTimer = 0f;
    private float spawnInterval = 2.0f; // Спавнить врага каждые 2 секунды

    public GameLoop() {
        this.enemies = new Array<>();
        this.towers = new Array<>();
        this.projectiles = new Array<>();
        this.projectilesToSpawn = new Array<>();

        initLevelPath();
    }

    /**
     * Временная инициализация пути для теста.
     * В будущем эти точки можно загружать из файла карты.
     */
    private void initLevelPath() {
        roadPath = new RoadPath();
        roadPath.addPoint(0, 400);     // Старт (левый край экрана)
        roadPath.addPoint(300, 400);   // Первый поворот
        roadPath.addPoint(300, 150);   // Второй поворот
        roadPath.addPoint(800, 150);   // Финиш (правый край экрана)
    }

    /**
     * Главный метод обновления, который должен вызываться
     * в методе render() твоего главного экрана (Screen).
     */
    public void update(float deltaTime) {
        // 1. Логика спавна врагов (автоматическая генерация волны для теста)
        handleEnemySpawning(deltaTime);

        // 2. Обновление врагов
        // Используем обратный цикл (с конца в начало), чтобы безопасно удалять объекты во время итерации
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(deltaTime);

            if (!enemy.isActive()) {
                enemies.removeIndex(i);
            }
        }

        // 3. Обновление башен
        // Передаем список врагов для поиска целей и буфер для новых снарядов
        projectilesToSpawn.clear();
        for (Tower tower : towers) {
            tower.update(deltaTime, enemies, projectilesToSpawn);
        }

        // Добавляем созданные башнями снаряды в общий игровой список
        projectiles.addAll(projectilesToSpawn);

        // 4. Обновление снарядов
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update(deltaTime);

            if (!projectile.isActive()) {
                projectiles.removeIndex(i);
            }
        }
    }

    /**
     * Пример простейшего спавнера врагов во времени
     */
    private void handleEnemySpawning(float deltaTime) {
        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval) {
            // Создаем врага на стартовой точке пути с поведением WalkMovement
            Enemy enemy = new Enemy(0, 400, 100f, 80f, new WalkMovement(roadPath));
            enemies.add(enemy);
            spawnTimer = 0f;
        }
    }

    // Публичный метод для добавления башни (будет вызываться из InteractionService)
    public void addTower(Tower tower) {
        towers.add(tower);
    }

    // Геттеры для слоя отрисовки (Presentation layer), чтобы рисовать объекты на экране
    public Array<Enemy> getEnemies() { return enemies; }
    public Array<Tower> getTowers() { return towers; }
    public Array<Projectile> getProjectiles() { return projectiles; }
    public RoadPath getRoadPath() { return roadPath; }
}
