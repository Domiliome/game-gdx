package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;
import io.github.simple_game.core.Main;
import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.items.Item;
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
    private final InventoryManager inventoryManager;

    private Tower selectedTower = null;
    private boolean isVictory = false;
    private boolean isPaused = false; // ВНЕДРЕНО: Флаг глобальной игровой паузы

    public GameLoop(Main game) {
        this.roadPath = new RoadPath();
        this.entityManager = new EntityManager();
        this.waveManager = new WaveManager(roadPath);
        this.currencyManager = new CurrencyManager(250, 20);
        this.gameGrid = new GameGrid(roadPath);
        this.shopService = new ShopService();
        this.inventoryManager = game.getGlobalInventory();
    }

    public void update(float deltaTime) {
        if (isVictory) return;

        // ВНЕДРЕНО: Если игра на паузе — полностью останавливаем такты менеджеров и движение мобов
        if (isPaused) return;

        waveManager.update(deltaTime, entityManager.getEnemies());
        entityManager.updateEntities(deltaTime, currencyManager, inventoryManager);

        // Лимит сессии. Если 20 волна успешно зачищена и врагов на карте нет — ПОБЕДА!
        if (waveManager.getCurrentWaveNumber() == 20 && !waveManager.isWaveActive() && entityManager.getEnemies().size == 0) {
            this.isVictory = true;
            System.out.println("🏆 СЕССИЯ ЗАВЕРШЕНА! ВЫ ОДОЛЕЛИ ВСЕ 20 ВОЛН!");
        }
    }

    public void addTower(Tower tower) {
        for (Item item : inventoryManager.getEquippedSlots()) {
            item.applyEffect(tower);
        }
        entityManager.addTower(tower);
    }

    // Геттер и сеттер для триггера паузы, вызываемые из TopStatusBar
    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { this.isPaused = paused; }

    public Tower getSelectedTower() { return selectedTower; }
    public void setSelectedTower(Tower tower) { this.selectedTower = tower; }
    public InventoryManager getInventoryManager() { return inventoryManager; }
    public boolean isVictory() { return isVictory; }

    public Array<Enemy> getEnemies() { return entityManager.getEnemies(); }
    public Array<Tower> getTowers() { return entityManager.getTowers(); }
    public Array<Projectile> getProjectiles() { return entityManager.getProjectiles(); }
    public RoadPath getRoadPath() { return roadPath; }
    public WaveManager getWaveManager() { return waveManager; }
    public CurrencyManager getCurrencyManager() { return currencyManager; }
    public GameGrid getGameGrid() { return gameGrid; }
    public ShopService getShopService() { return shopService; }
}
