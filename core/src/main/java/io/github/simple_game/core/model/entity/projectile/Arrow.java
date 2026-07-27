package io.github.simple_game.core.model.entity.projectile;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.tower.TowerType;
/**
 * Специализированный снаряд (стрела), выпускаемый башней лучников.
 * Хранит информацию о том, является ли выстрел критическим, для последующей
 * визуализации и расчета урона.
 */
public class Arrow extends Projectile {
    private final boolean isCritical;

    /**
     * Создает новую стрелу.
     */
    public Arrow(float x, float y, Enemy target, float damage, TowerType towerType, boolean isCritical) {
        super(x, y, target, damage, towerType);
        this.isCritical = isCritical;
    }

    /**
     * @return true, если эта стрела наносит критический урон; иначе false
     */
    public boolean isCritical() {
        return isCritical;
    }
}
