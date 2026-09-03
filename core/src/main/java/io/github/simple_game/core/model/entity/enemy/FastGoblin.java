package io.github.simple_game.core.model.entity.enemy;

/**
 * Класс быстрого гоблина (TIER_1_LIGHT).
 */
public class FastGoblin extends Enemy {

    public FastGoblin(float x, float y, float hpMod, float speedMod) {
        super(x, y);
        this.health = 50f * hpMod;
        this.speed = 130f * speedMod;
        this.goldReward = 8;
        this.tier = EnemyTier.TIER_1_LIGHT;
        initSprite(1f);
    }

    @Override
    public final String getSpritePath() {
        return "enemies/goblin.png";
    }
}
