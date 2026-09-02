package io.github.simple_game.core.model.entity.enemy;

public class NormalZombie extends Enemy {
    public NormalZombie(float x, float y, float hpMod, float speedMod) {
        super(x, y);
        this.health = 100f * hpMod;
        this.speed = 70f * speedMod;
        this.goldReward = 25;
        this.tier = EnemyTier.TIER_2_NORMAL;
        initSprite(getDefaultVisualScale());
    }

    @Override
    public String getSpritePath() {
        return "enemies/zombie.png";
    }
}
