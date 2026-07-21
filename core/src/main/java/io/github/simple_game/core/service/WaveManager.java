package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.model.movement.WalkMovement;

/**
 * Менеджер управления волнами наступающих врагов.
 * Отвечает за контроль временных интервалов между волнами, расчет прогрессии
 * сложности мобов (увеличение очков здоровья и скорости) и порционный спавн
 * юнитов на карту по таймеру.
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

    /**
     * Создает новый менеджер волн, привязанный к заданному маршруту уровня.
     *
     * @param roadPath маршрут движения, на стартовой точке которого будут появляться враги
     */
    public WaveManager(RoadPath roadPath) {
        this.roadPath = roadPath;
    }

    /**
     * Обновляет состояние менеджера волн каждый такт игрового цикла.
     * В зависимости от фазы игры осуществляет отсчет таймера до старта следующей волны
     * либо координирует генерацию мобов и проверяет условия полной зачистки волны.
     *
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     * @param enemies   актуальный список живых врагов из игрового цикла для контроля зачистки
     */
    public void update(float deltaTime, Array<Enemy> enemies) {
        if (isWaveActive) {
            handleSpawning(deltaTime, enemies);

            if (enemiesLeftToSpawn == 0 && enemies.size == 0) {
                isWaveActive = false;
                waveTimer = 0f;
                System.out.println("Wave " + currentWaveNumber + " cleared!");
            }
        } else {
            waveTimer += deltaTime;
            if (waveTimer >= timeBetweenWaves) {
                startNextWave();
            }
        }
    }

    /**
     * Осуществляет перевод игры в фазу активного боя.
     * Инкрементирует счетчик раундов, обнуляет спавн-таймеры и рассчитывает общее
     * количество врагов в наступающей волне на основе математической прогрессии.
     */
    private void startNextWave() {
        currentWaveNumber++;
        isWaveActive = true;

        enemiesLeftToSpawn = 3 + currentWaveNumber * 2;
        spawnTimer = 0f;

        System.out.println("Start wave " + currentWaveNumber + "! Enemy: " + enemiesLeftToSpawn);
    }

    /**
     * Внутренний метод порционной генерации юнитов внутри активной волны.
     * Отсчитывает фиксированный интервал, рассчитывает динамические характеристики
     * здоровья и скорости врага в зависимости от номера текущей волны и добавляет
     * готовый объект на карту.
     *
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     * @param enemies   список игрового цикла для регистрации созданного врага
     */
    private void handleSpawning(float deltaTime, Array<Enemy> enemies) {
        if (enemiesLeftToSpawn <= 0) return;

        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval) {
            float health = 80f + (currentWaveNumber * 20f);
            float speed = 70f + Math.min(50f, currentWaveNumber * 5f);

            Enemy enemy = new Enemy(0, 400, health, speed, new WalkMovement(roadPath));
            enemies.add(enemy);

            enemiesLeftToSpawn--;
            spawnTimer = 0f;
        }
    }

    /**
     * @return порядковый номер текущей или последней пройденной волны
     */
    public int getCurrentWaveNumber() { return currentWaveNumber; }

    /**
     * @return true, если в данный момент идет сражение; false, если идет фаза перерыва и подготовки
     */
    public boolean isWaveActive() { return isWaveActive; }

    /**
     * Возвращает время, оставшееся до автоматического старта следующей волны.
     *
     * @return количество секунд до начала набега (не меньше 0)
     */
    public float getTimeUntilNextWave() { return Math.max(0f, timeBetweenWaves - waveTimer); }
}
