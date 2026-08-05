package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.projectile.MagicSphere;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;

public class MagicTower extends Tower {
    private static final float BASE_DAMAGE = 10f;
    private static final float BASE_RANGE = 130f;
    private static final float BASE_COOLDOWN = 1.2f;

    public MagicTower(float x, float y, GameLoop gameLoop) {
        super(x, y, TowerType.MAGIC, gameLoop);
        this.damage = BASE_DAMAGE;
        this.attackRange = BASE_RANGE;
        this.attackCooldown = BASE_COOLDOWN;
        Texture sheet = new Texture(Gdx.files.internal("tower_magic_init.png"));
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);

        int totalFrames = tmp[0].length;
        TextureRegion[] animationFrames = new TextureRegion[totalFrames];

        System.arraycopy(tmp[0], 0, animationFrames, 0, totalFrames);


        this.initAnimation = new Animation<>(0.06f, animationFrames);
    }

    @Override
    public void tryUpgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            this.damage = BASE_DAMAGE * currentLevel;
            this.attackRange = BASE_RANGE + (currentLevel - 1) * 15f;

            this.baseDamage = this.damage;
            this.baseAttackRange = this.attackRange;
            this.baseAttackCooldown = this.attackCooldown;
            System.out.println("Магическая башня улучшена до уровня " + currentLevel);
        }
    }

    @Override
    public int getUpgradeCost() {
        return (int) (type.getCost() * 0.8f * currentLevel);
    }

    @Override
    protected void shoot(Array<Projectile> projectilesToSpawn) {
        Projectile sphere = new MagicSphere(position.x, position.y, target, damage, type);
        projectilesToSpawn.add(sphere);
    }

    public TextureRegion getCurrentInitFrame() {
        return initAnimation != null ? initAnimation.getKeyFrame(animationTime) : null;
    }

    public boolean isInitializing() {
        return isInitializing;
    }
}
