package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.movement.RoadPath;

/**
 * Менеджер управления волнами наступающих врагов.
 */
public class WaveManager {
    private final RoadPath roadPath;
    private int currentWaveNumber = 0;
    private int enemiesLeftToSpawn = 0;

    private float spawnTimer = 0f;
    private final float spawnInterval = 1.0f;
    private float waveTimer = 0f;
    private final float timeBetweenWaves = 10f;
    private boolean isWaveActive = false;

    public WaveManager(RoadPath roadPath) {
        this.roadPath = roadPath;
    }

    // Измените метод update() в WaveManager.java:
    public void update(float deltaTime, Array<Enemy> enemies) {
        if (isWaveActive) {
            handleSpawning(deltaTime, enemies);

            if (enemiesLeftToSpawn == 0 && enemies.size == 0) {
                isWaveActive = false;
                waveTimer = 0f;
                System.out.println("Wave " + currentWaveNumber + " cleared!");
            }
        }
        // УДАЛЕНО: автоматический отсчет и вызов startNextWave() по таймеру
    }

    // Делаем метод публичным, чтобы интерфейс мог вызвать его при клике:
    public void startNextWave() {
        if (isWaveActive) return; // Защита: нельзя запустить волну, пока идет бой
        currentWaveNumber++;
        isWaveActive = true;
        enemiesLeftToSpawn = 4 + currentWaveNumber * 2;
        spawnTimer = 0f;
        System.out.println("Start wave " + currentWaveNumber + "! Enemy: " + enemiesLeftToSpawn);
    }


    private void handleSpawning(float deltaTime, Array<Enemy> enemies) {
        if (enemiesLeftToSpawn <= 0) return;

        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval) {
            // Делегируем создание классического наземного врага фабрике
            Enemy enemy = EnemyFactory.createEnemy(currentWaveNumber, roadPath);
            enemies.add(enemy);

            enemiesLeftToSpawn--;
            spawnTimer = 0f;
        }
    }

    public int getCurrentWaveNumber() { return currentWaveNumber; }
    public boolean isWaveActive() { return isWaveActive; }
    public float getTimeUntilNextWave() { return Math.max(0f, timeBetweenWaves - waveTimer); }
}
