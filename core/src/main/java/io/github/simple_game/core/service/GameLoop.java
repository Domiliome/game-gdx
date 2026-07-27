package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;
import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.map.GameGrid;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.movement.RoadPath;

/**
 * Ядро игрового процесса (Центральный игровой цикл).
 * Координирует работу подсистем и менеджеров верхнего уровня.
 */
public class GameLoop {
    private final EntityManager entityManager;
    private final WaveManager waveManager;
    private final CurrencyManager currencyManager;
    private final GameGrid gameGrid;
    private final ShopService shopService;
    private final RoadPath roadPath; // Сделали final

    public GameLoop() {
        // Создаем пустой маршрут. Точки в него запишет GameScreen.resize() динамически
        this.roadPath = new RoadPath();

        this.entityManager = new EntityManager();
        this.waveManager = new WaveManager(roadPath);
        this.currencyManager = new CurrencyManager(250, 20);
        this.gameGrid = new GameGrid(roadPath);
        this.shopService = new ShopService();
    }

    /**
     * Главный метод такта игры, координирующий логику обновления менеджеров.
     */
    public void update(float deltaTime) {
        waveManager.update(deltaTime, entityManager.getEnemies());
        entityManager.updateEntities(deltaTime, currencyManager);
    }

    public void addTower(Tower tower) {
        entityManager.addTower(tower);
    }

    // Пробрасываем геттеры сущностей наружу для рендереров
    public Array<Enemy> getEnemies() { return entityManager.getEnemies(); }
    public Array<Tower> getTowers() { return entityManager.getTowers(); }
    public Array<Projectile> getProjectiles() { return entityManager.getProjectiles(); }

    // Геттеры сервисов
    public RoadPath getRoadPath() { return roadPath; }
    public WaveManager getWaveManager() { return waveManager; }
    public CurrencyManager getCurrencyManager() { return currencyManager; }
    public GameGrid getGameGrid() { return gameGrid; }
    public ShopService getShopService() { return shopService; }
}
