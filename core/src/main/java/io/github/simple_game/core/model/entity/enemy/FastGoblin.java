package io.github.simple_game.core.model.entity.enemy;

/**
 * Класс быстрого гоблина (TIER_1_LIGHT).
 * Имеет повышенную скорость передвижения, но крайне малый запас здоровья.
 */
public class FastGoblin extends Enemy {

    public FastGoblin(float x, float y, float hpMod, float speedMod) {
        super(x, y);
        this.health = 50f * hpMod;
        this.speed = 130f * speedMod;
        this.goldReward = 15;
        this.tier = EnemyTier.TIER_1_LIGHT;
    }
}
