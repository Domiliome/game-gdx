package io.github.simple_game.core.model.entity.projectile;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.tower.TowerType;

/**
 * Артиллерийское ядро со взрывным уроном по площади.
 */
public class CannonBall extends Projectile {
    private final Array<Enemy> enemies;
    private static final float BLAST_RADIUS = 70f;

    public CannonBall(float x, float y, Enemy target, float damage, TowerType towerType, Array<Enemy> enemies) {
        super(x, y, target, damage, towerType);
        this.enemies = enemies;
    }

    @Override
    protected void hitTarget() {
        active = false;
        float explosionX = position.x;
        float explosionY = position.y;
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy currentEnemy = enemies.get(i);
            float distanceToExplosion = currentEnemy.getPosition().dst(explosionX, explosionY);
            if (distanceToExplosion <= BLAST_RADIUS) {
                currentEnemy.takeDamage(damage);
            }
        }
    }
}
