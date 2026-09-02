package io.github.simple_game.core.service;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.CombatWorld;
import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.model.entity.map.GameGrid;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.movement.PathGenerator;
import io.github.simple_game.core.model.movement.PathType;
import io.github.simple_game.core.model.movement.RoadPath;

public class GameLoop implements CombatWorld {
    private final EntityManager entityManager;
    private final WaveManager waveManager;
    private final CurrencyManager currencyManager;
    private final GameGrid gameGrid;
    private final ShopService shopService;
    private final RoadPath roadPath;
    private final InventoryManager inventoryManager;

    private Tower selectedTower = null;
    private boolean isVictory = false;
    private boolean isPaused = false;

    public GameLoop(InventoryManager inventoryManager) {
        this.roadPath = new RoadPath();
        this.entityManager = new EntityManager();
        this.waveManager = new WaveManager(roadPath);
        this.currencyManager = new CurrencyManager(10000, 20);
        this.gameGrid = new GameGrid(roadPath);
        this.shopService = new ShopService();
        this.inventoryManager = inventoryManager;

        PathType[] types = PathType.values();
        PathGenerator.generate(roadPath, types[MathUtils.random(0, types.length - 1)]);
    }

    public void update(float deltaTime) {
        if (isVictory) return;
        if (isPaused) return;

        waveManager.update(deltaTime, entityManager.getEnemies());
        entityManager.updateEntities(deltaTime, currencyManager, inventoryManager);

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

    public boolean isPaused() { return isPaused; }
    public void setPaused(boolean paused) { this.isPaused = paused; }

    public Tower getSelectedTower() { return selectedTower; }
    public void setSelectedTower(Tower tower) { this.selectedTower = tower; }
    public InventoryManager getInventoryManager() { return inventoryManager; }
    public boolean isVictory() { return isVictory; }

    @Override
    public Array<Enemy> getEnemies() { return entityManager.getEnemies(); }

    @Override
    public Array<Tower> getTowers() { return entityManager.getTowers(); }

    @Override
    public Array<Item> getEquippedItems() { return inventoryManager.getEquippedSlots(); }

    public Array<Projectile> getProjectiles() { return entityManager.getProjectiles(); }
    public RoadPath getRoadPath() { return roadPath; }
    public WaveManager getWaveManager() { return waveManager; }
    public CurrencyManager getCurrencyManager() { return currencyManager; }
    public GameGrid getGameGrid() { return gameGrid; }
    public ShopService getShopService() { return shopService; }
}
