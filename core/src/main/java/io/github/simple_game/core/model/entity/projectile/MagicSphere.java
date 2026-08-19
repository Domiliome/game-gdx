    package io.github.simple_game.core.model.entity.projectile;

    import io.github.simple_game.core.model.entity.enemy.Enemy;
    import io.github.simple_game.core.model.entity.tower.TowerType;
    import io.github.simple_game.core.service.CurrencyManager;

    /**
     * Специализированный магический снаряд, который наносит урон
     * и активирует эффект замедления на вражеском юните.
     */
    public class MagicSphere extends Projectile {

        public MagicSphere(float x, float y, Enemy target, float damage, TowerType towerType) {
            super(x, y, target, damage, towerType);
        }

        @Override
        protected void hitTarget(CurrencyManager economy) {
            // Сначала активируем замедление на враге (на 3 секунды снижаем скорость на 50%)
            target.applySlow(0.5f, 3.0f);
            // Затем вызываем базовое нанесение урона и деактивацию снаряда
            super.hitTarget(economy);
        }
    }
