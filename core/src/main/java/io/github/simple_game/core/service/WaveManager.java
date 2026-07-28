package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.enemy.EnemyTier;
import io.github.simple_game.core.model.movement.RoadPath;

/**
 * Менеджер управления волнами наступающих врагов.
 * Автоматически рассчитывает микс разных тиров монстров на основе бюджета сложности.
 */
public class WaveManager {
    private final RoadPath roadPath;
    private final Array<EnemyTier> spawnQueue = new Array<>(); // Очередь тиров на спавн

    private int currentWaveNumber = 0;
    private float spawnTimer = 0f;
    private final float spawnInterval = 1.0f;
    private boolean isWaveActive = false;

    public WaveManager(RoadPath roadPath) {
        this.roadPath = roadPath;
    }

    public void update(float deltaTime, Array<Enemy> enemies) {
        if (isWaveActive) {
            handleSpawning(deltaTime, enemies);

            // Волна зачищена, если очередь спавна пуста и на карте не осталось живых врагов
            if (spawnQueue.size == 0 && enemies.size == 0) {
                isWaveActive = false;
                System.out.println("Wave " + currentWaveNumber + " cleared!");
            }
        }
    }

    public void startNextWave() {
        if (isWaveActive) return;
        currentWaveNumber++;
        isWaveActive = true;
        spawnTimer = 0f;
        spawnQueue.clear();

        // Формула бюджета сложности волны (растет с каждым раундом)
        int waveBudget = 4 + currentWaveNumber * 3;

        // Распределяем тиры врагов, пока не исчерпаем бюджет раунда
        while (waveBudget > 0) {
            if (currentWaveNumber >= 5 && waveBudget >= EnemyTier.TIER_3_HEAVY.getWeight() && com.badlogic.gdx.math.MathUtils.randomBoolean(0.2f)) {
                spawnQueue.add(EnemyTier.TIER_3_HEAVY);
                waveBudget -= EnemyTier.TIER_3_HEAVY.getWeight();
            } else if (currentWaveNumber >= 3 && waveBudget >= EnemyTier.TIER_2_NORMAL.getWeight() && com.badlogic.gdx.math.MathUtils.randomBoolean(0.4f)) {
                spawnQueue.add(EnemyTier.TIER_2_NORMAL);
                waveBudget -= EnemyTier.TIER_2_NORMAL.getWeight();
            } else {
                spawnQueue.add(EnemyTier.TIER_1_LIGHT);
                waveBudget -= EnemyTier.TIER_1_LIGHT.getWeight();
            }
        }

        // Случайно перемешиваем очередь, чтобы враги шли вперемешку
        spawnQueue.shuffle();
        System.out.println("Start wave " + currentWaveNumber + "! Total units: " + spawnQueue.size);
    }

    private void handleSpawning(float deltaTime, Array<Enemy> enemies) {
        if (spawnQueue.size == 0) return;

        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval) {
            // Извлекаем следующий тир из начала очереди
            EnemyTier nextTier = spawnQueue.removeIndex(0);


            Enemy enemy = EnemyFactory.createEnemy(nextTier, currentWaveNumber, roadPath);
            enemies.add(enemy);

            spawnTimer = 0f;
        }
    }

    public int getCurrentWaveNumber() { return currentWaveNumber; }
    public boolean isWaveActive() { return isWaveActive; }
}
