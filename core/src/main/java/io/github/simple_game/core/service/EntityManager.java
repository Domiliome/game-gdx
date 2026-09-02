package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.model.entity.tower.Tower;

/**
 * Менеджер сущностей игрового мира.
 * Отвечает за хранение, добавление и безопасную очистку мертвых объектов.
 */
public class EntityManager {
    private final Array<Enemy> enemies = new Array<>();
    private final Array<Tower> towers = new Array<>();
    private final Array<Projectile> projectiles = new Array<>();
    private final Array<Projectile> projectilesToSpawn = new Array<>();

    public void updateEntities(float deltaTime, CurrencyManager currencyManager, InventoryManager inventoryManager) {
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(deltaTime);

            if (!enemy.isActive()) {
                if (enemy.getHealth() <= 0) {
                    currencyManager.addGold(enemy.getGoldReward());
                    inventoryManager.calculateLootDrop(enemy);
                } else {
                    currencyManager.decreaseLives(1);
                }
                enemies.removeIndex(i);
            }
        }

        projectilesToSpawn.clear();
        for (int i = 0; i < towers.size; i++) {
            towers.get(i).update(deltaTime, enemies, projectilesToSpawn);
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

    public void addTower(Tower tower) { towers.add(tower); }
    public void removeTower(Tower tower) {
        if (tower != null) {
            tower.onRemoved();
            towers.removeValue(tower, true);
        }
    }
    public Array<Enemy> getEnemies() { return enemies; }
    public Array<Tower> getTowers() { return towers; }
    public Array<Projectile> getProjectiles() { return projectiles; }
}
