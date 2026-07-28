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

    public void updateEntities(float deltaTime, CurrencyManager currencyManager) {
        // 1. Обновление и безопасное удаление врагов
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(deltaTime, currencyManager);
            if (!enemy.isActive()) enemies.removeIndex(i);
        }

        // 2. ИИ башен и сбор новых снарядов
        projectilesToSpawn.clear();
        for (Tower tower : towers) {
            tower.update(deltaTime, enemies, projectilesToSpawn);
        }
        projectiles.addAll(projectilesToSpawn);

        // 3. Обновление и безопасное удаление снарядов
        for (int i = projectiles.size - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile.update(deltaTime, currencyManager);
            if (!projectile.isActive()) projectiles.removeIndex(i);
        }
    }

    public void addTower(Tower tower) { towers.add(tower); }
    public Array<Enemy> getEnemies() { return enemies; }
    public Array<Tower> getTowers() { return towers; }
    public Array<Projectile> getProjectiles() { return projectiles; }
}
