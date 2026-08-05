package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.projectile.Arrow;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;

public class ArcherTower extends Tower {
    private static final float BASE_DAMAGE = 15f;
    private static final float BASE_RANGE = 150f;
    private static final float BASE_COOLDOWN = 0.6f;
    private final float damageMultiplier = 1.15f;
    private final float rangeMultiplier = 1.05f;
    private final float cooldownReduction = 0.90f;
    private float critChance = 0.20f;
    private final float critDamageMultiplier = 2.0f;

    public ArcherTower(float x, float y, GameLoop gameLoop) {
        super(x, y, TowerType.ARCHER, gameLoop);
        this.damage = BASE_DAMAGE;
        this.attackRange = BASE_RANGE;
        this.attackCooldown = BASE_COOLDOWN;

        Texture sheet = new Texture(Gdx.files.internal("tower_archer_init.png"));
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);

        int totalFrames = tmp[0].length;
        TextureRegion[] animationFrames = new TextureRegion[totalFrames];

        // ИСПРАВЛЕНО: Заменили ручной цикл for на быстрый системный arraycopy, чтобы полностью убрать ворнинг "Manual array copy"
        System.arraycopy(tmp[0], 0, animationFrames, 0, totalFrames);

        // ИСПРАВЛЕНО: Убрали избыточный тип данных в выражении создания объекта, заменив его на алмазный оператор <>
        this.initAnimation = new Animation<>(0.06f, animationFrames);
    }

    @Override
    public void tryUpgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            this.damage = BASE_DAMAGE * (float) Math.pow(damageMultiplier, currentLevel - 1);
            this.attackRange = BASE_RANGE * (float) Math.pow(rangeMultiplier, currentLevel - 1);
            this.attackCooldown = Math.max(0.1f, BASE_COOLDOWN * (float) Math.pow(cooldownReduction, currentLevel - 1));

            this.baseDamage = this.damage;
            this.baseAttackRange = this.attackRange;
            this.baseAttackCooldown = this.attackCooldown;

            this.critChance = 0.20f + (currentLevel - 1) * 0.05f;
            System.out.println("Башня улучшена до уровня " + currentLevel);
        }
    }

    @Override
    public int getUpgradeCost() {
        return (int) (type.getCost() * 0.5f * currentLevel);
    }

    @Override
    protected void shoot(Array<Projectile> projectilesToSpawn) {
        boolean isCrit = Math.random() < critChance;
        float finalDamage = isCrit ? this.damage * critDamageMultiplier : this.damage;
        Projectile arrow = new Arrow(position.x, position.y, target, finalDamage, type, isCrit);
        projectilesToSpawn.add(arrow);
    }

    public TextureRegion getCurrentInitFrame() {
        return initAnimation != null ? initAnimation.getKeyFrame(animationTime) : null;
    }

    public boolean isInitializing() { return isInitializing; }
}
