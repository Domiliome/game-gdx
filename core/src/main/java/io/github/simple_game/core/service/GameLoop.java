package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.map.GameGrid;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.movement.RoadPath;

public class GameLoop {
    private final EntityManager entityManager;
    private final WaveManager waveManager;
    private final CurrencyManager currencyManager;
    private final GameGrid gameGrid;
    private final ShopService shopService;
    private final RoadPath roadPath;
    private final InventoryManager inventoryManager; // Зарегистрировали поле

    private Tower selectedTower = null;

    public GameLoop() {
        this.roadPath = new RoadPath();
        this.entityManager = new EntityManager();
        this.waveManager = new WaveManager(roadPath);
        this.currencyManager = new CurrencyManager(250, 20);
        this.gameGrid = new GameGrid(roadPath);
        this.shopService = new ShopService();
        this.inventoryManager = new InventoryManager(); // Инициализируем менеджер лута
    }

    public void update(float deltaTime) {
        waveManager.update(deltaTime, entityManager.getEnemies());
        entityManager.updateEntities(deltaTime, currencyManager, inventoryManager);

    }
// ИСПРАВЛЕНО: Вернули чистый метод. Теперь параметры башни гарантированно не обнулятся!
public void addTower(Tower tower) {
    entityManager.addTower(tower);
}


    public Tower getSelectedTower() { return selectedTower; }
    public void setSelectedTower(Tower tower) { this.selectedTower = tower; }

    // Добавили геттер инвентаря — ошибка "cannot find symbol" в InventoryScreen полностью исчезнет!
    public InventoryManager getInventoryManager() { return inventoryManager; }

    public Array<Enemy> getEnemies() { return entityManager.getEnemies(); }
    public Array<Tower> getTowers() { return entityManager.getTowers(); }
    public Array<Projectile> getProjectiles() { return entityManager.getProjectiles(); }

    public RoadPath getRoadPath() { return roadPath; }
    public WaveManager getWaveManager() { return waveManager; }
    public CurrencyManager getCurrencyManager() { return currencyManager; }
    public GameGrid getGameGrid() { return gameGrid; }
    public ShopService getShopService() { return shopService; }
}
