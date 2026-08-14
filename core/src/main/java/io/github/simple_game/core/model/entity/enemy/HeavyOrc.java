package io.github.simple_game.core.model.entity.enemy;

public class HeavyOrc extends Enemy {
    public HeavyOrc(float x, float y, float hpMod, float speedMod) {
        super(x, y);
        this.health = 300f * hpMod;
        this.speed = 40f * speedMod;
        this.goldReward = 60;
        this.tier = EnemyTier.TIER_3_HEAVY;
        initSprite(getSpritePath(), getDefaultVisualScale());
    }

    @Override
    protected String getSpritePath() {
        return "enemies/orc.png";
    }

    @Override
    protected float getDefaultVisualScale() {
        return 1.25f;
    }
}
