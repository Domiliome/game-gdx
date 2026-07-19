package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.Projectile;
import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.model.movement.RoadPath;

public class GameLoop {
    private final Array<Enemy> enemies;
    private final Array<Tower> towers;
    private final Array<Projectile> projectiles;
    private final Array<Projectile> projectilesToSpawn;

    private RoadPath roadPath;
    private final WaveManager waveManager;

    public GameLoop() {
        this.enemies = new Array<>();
        this.towers = new Array<>();
        this.projectiles = new Array<>();
        this.projectilesToSpawn = new Array<>();

        initLevelPath();
        this.waveManager = new WaveManager(roadPath);
    }

    private void initLevelPath() {
        roadPath = new RoadPath();
        roadPath.addPoint(0, 400);
        roadPath.addPoint(300, 400);
        roadPath.addPoint(300, 150);
        roadPath.addPoint(800, 150);
    }

    public void update(float deltaTime) {
        waveManager.update(deltaTime, enemies);

        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(deltaTime);

            if (!enemy.isActive()) {
                enemies.removeIndex(i);
            }
        }

        projectilesToSpawn.clear();
        for (Tower tower : towers) {
            tower.update(deltaTime, enemies, projectilesToSpawn);
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

    public void addTower(Tower tower) {
        towers.add(tower);
    }

    public Array<Enemy> getEnemies() { return enemies; }
    public Array<Tower> getTowers() { return towers; }
    public Array<Projectile> getProjectiles() { return projectiles; }
    public RoadPath getRoadPath() { return roadPath; }
    public WaveManager getWaveManager() { return waveManager; }

}
