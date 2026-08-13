package io.github.simple_game.core.model.entity.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import io.github.simple_game.core.service.CurrencyManager;

/**
 * Класс быстрого гоблина (TIER_1_LIGHT).
 * Автоматически рассчитывает точный угол поворота во все 4 стороны в зависимости от движения.
 */
public class FastGoblin extends Enemy {

    private final Animation<TextureRegion> runAnimation;
    private float animationTime = 0f;

    private final Vector2 lastPosition = new Vector2();
    private float currentRotation = 0f;

    public FastGoblin(float x, float y, float hpMod, float speedMod) {
        super(x, y);
        this.health = 50f * hpMod;
        this.speed = 130f * speedMod;
        this.goldReward = 15;
        this.tier = EnemyTier.TIER_1_LIGHT;
        this.lastPosition.set(x, y);

        Texture sheet = new Texture(Gdx.files.internal("goblin.png"));
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);

        int totalFrames = tmp[0].length;
        TextureRegion[] animationFrames = new TextureRegion[totalFrames];

        for (int i = 0; i < totalFrames; i++) {
            animationFrames[i] = tmp[0][i];
        }

        this.runAnimation = new Animation<>(0.08f, animationFrames);
        this.runAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float deltaTime, CurrencyManager economy) {
        super.update(deltaTime, economy);
        if (isActive()) {
            animationTime += deltaTime;
        }
    }

    public TextureRegion getCurrentGoblinFrame() {
        if (runAnimation == null || !isActive()) return null;

        TextureRegion frame = runAnimation.getKeyFrame(animationTime);
        if (frame == null) return null;

        float deltaX = position.x - lastPosition.x;
        float deltaY = position.y - lastPosition.y;


        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Движение преимущественно по горизонтали
            if (deltaX > 0.1f) {
                currentRotation = 90f;   // Бежит направо
            } else if (deltaX < -0.1f) {
                currentRotation = 270f; // Бежит налево
            }
        } else {
            // Движение преимущественно по вертикали
            if (deltaY > 0.1f) {
                currentRotation = 180f;  // Бежит вверх
            } else if (deltaY < -0.1f) {
                currentRotation = 0f; // Бежит вниз
            }
        }

        lastPosition.set(position.x, position.y);

        return frame;
    }

    public float getCurrentRotation() {
        return currentRotation;
    }
}
