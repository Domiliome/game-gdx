package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.model.movement.WalkMovement;

public class WaveManager {
    private final RoadPath roadPath;

    private int currentWaveNumber = 0;
    private int enemiesLeftToSpawn = 0;

    private float spawnTimer = 0f;
    private final float spawnInterval = 1.0f; // Интервал между спавном врагов внутри волны

    private float waveTimer = 0f;
    private final float timeBetweenWaves = 10f; // Перерыв между волнами в секундах

    private boolean isWaveActive = false;

    public WaveManager(RoadPath roadPath) {
        this.roadPath = roadPath;
    }

    public void update(float deltaTime, Array<Enemy> enemies) {
        if (isWaveActive) {
            handleSpawning(deltaTime, enemies);

            // Если всех заспавнили и на карте не осталось живых врагов — волна завершена
            if (enemiesLeftToSpawn == 0 && enemies.size == 0) {
                isWaveActive = false;
                waveTimer = 0f;
                System.out.println("Волна " + currentWaveNumber + " зачищена!");
            }
        } else {
            // Отсчет времени до следующей волны
            waveTimer += deltaTime;
            if (waveTimer >= timeBetweenWaves) {
                startNextWave();
            }
        }
    }

    private void startNextWave() {
        currentWaveNumber++;
        isWaveActive = true;

        // С каждым уровнем количество врагов увеличивается (например, 5, 8, 11...)
        enemiesLeftToSpawn = 3 + currentWaveNumber * 2;
        spawnTimer = 0f;

        System.out.println("Началась волна №" + currentWaveNumber + "! Врагов: " + enemiesLeftToSpawn);
    }

    private void handleSpawning(float deltaTime, Array<Enemy> enemies) {
        if (enemiesLeftToSpawn <= 0) return;

        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval) {
            // Динамически рассчитываем силу врагов в зависимости от номера волны
            float health = 80f + (currentWaveNumber * 20f); // +20 HP каждую волну
            float speed = 70f + Math.min(50f, currentWaveNumber * 5f); // Немного ускоряем

            Enemy enemy = new Enemy(0, 400, health, speed, new WalkMovement(roadPath));
            enemies.add(enemy);

            enemiesLeftToSpawn--;
            spawnTimer = 0f;
        }
    }

    public int getCurrentWaveNumber() { return currentWaveNumber; }
    public boolean isWaveActive() { return isWaveActive; }
    public float getTimeUntilNextWave() { return Math.max(0f, timeBetweenWaves - waveTimer); }
}
