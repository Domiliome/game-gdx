package io.github.simple_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RedBall {
    private float centerX;
    private float centerY;
    private float vx = 0f;
    private float vy = 0f;
    private final float FRICTION = 0.97f;
    private final float BOUNCE = 0.7f;

    private final float GRAVITY = -1200f;

    // Жестко фиксированный базовый радиус (размер больше не меняется от HP)
    private final float BASE_RADIUS = 32f;
    private float currentRadius;
    private float targetRadius;
    private final float ANIMATION_SPEED = 10f;

    private float hp = 200f;
    private float maxHp = 200f;

    private final Rectangle bounds;
    private boolean isDragging;
    private float offsetX;
    private float offsetY;
    private final Vector3 touchPoint;

    private final Viewport viewport;
    private final float padding;

    private final Texture texture;

    public RedBall(float startX, float startY, Viewport viewport, float padding) {
        this.centerX = startX;
        this.centerY = startY;
        this.viewport = viewport;
        this.padding = padding;

        this.texture = new Texture("red_ball.png");

        this.currentRadius = BASE_RADIUS;
        this.targetRadius = BASE_RADIUS;
        this.bounds = new Rectangle(0, 0, BASE_RADIUS * 2f, BASE_RADIUS * 2f);
        this.isDragging = false;
        this.touchPoint = new Vector3();
    }

    public void update(float step) {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPoint);

            if (!isDragging) {
                // Прямоугольник нажатия теперь всегда стабилен и рассчитывается по базовому радиусу
                bounds.set(centerX - BASE_RADIUS, centerY - BASE_RADIUS, BASE_RADIUS * 2f, BASE_RADIUS * 2f);

                if (bounds.contains(touchPoint.x, touchPoint.y) && touchPoint.y < (viewport.getWorldHeight() * 0.4f)) {
                    isDragging = true;
                    offsetX = touchPoint.x - centerX;
                    offsetY = touchPoint.y - centerY;
                    vx = 0f;
                    vy = 0f;
                }
            }
        } else {
            if (isDragging) {
                isDragging = false;
            }
        }

        if (isDragging) {
            targetRadius = BASE_RADIUS * 1.7f;

            float oldCenterX = centerX;
            float oldCenterY = centerY;

            float desiredX = touchPoint.x - offsetX;
            float desiredY = touchPoint.y - offsetY;

            centerX = MathUtils.clamp(desiredX, padding + currentRadius, viewport.getWorldWidth() - padding - currentRadius);

            float maxAllowedY = (viewport.getWorldHeight() * 0.4f) - padding - currentRadius;
            float minY = padding + currentRadius;

            if (desiredY <= maxAllowedY) {
                centerY = MathUtils.clamp(desiredY, minY, maxAllowedY);
            } else {
                float rubberY = MathUtils.lerp(maxAllowedY, desiredY, 0.15f);
                centerY = MathUtils.clamp(rubberY, maxAllowedY, maxAllowedY + 50f);
            }

            vx = (centerX - oldCenterX) * 45f;
            vy = (centerY - oldCenterY) * 45f;

        } else {
            // ИСПРАВЛЕНО: Целевой радиус в свободном полете теперь всегда равен константе BASE_RADIUS
            targetRadius = BASE_RADIUS;

            vy += GRAVITY * step;
            centerX += vx * step;
            centerY += vy * step;

            vx *= Math.pow(FRICTION, step * 60);
            vy *= Math.pow(FRICTION, step * 60);

            if (Math.abs(vx) < 5f) vx = 0f;

            float minX = padding + currentRadius;
            float maxX = viewport.getWorldWidth() - padding - currentRadius;
            if (centerX < minX) { centerX = minX; vx = -vx * BOUNCE; }
            else if (centerX > maxX) { centerX = maxX; vx = -vx * BOUNCE; }

            float minY = padding + currentRadius;
            float maxY = viewport.getWorldHeight() - padding - currentRadius;
            ballCenterYCheck(minY, maxY);
        }

        currentRadius = MathUtils.lerp(currentRadius, targetRadius, ANIMATION_SPEED * deltaTime);
    }

    private boolean ballCenterYCheck(float minY, float maxY) {
        if (centerY < minY) {
            centerY = minY;
            vy = -vy * BOUNCE;
            if (Math.abs(vy) < 40f) vy = 0f;
            return true;
        }
        else if (centerY > maxY) {
            centerY = maxY;
            vy = -vy * BOUNCE;
            return true;
        }
        return false;
    }

    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // Отрисовка спрайта рассчитывается строго по текущему интерполированному значению
        float diameter = currentRadius * 2f;
        float drawX = centerX - currentRadius;
        float drawY = centerY - currentRadius;

        batch.draw(texture, drawX, drawY, diameter, diameter);

        // Интерфейс шкалы здоровья (отрисовка привязана к верхнему краю стабильного шара)
        if (hp < maxHp && hp > 0f) {
            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            float barWidth = BASE_RADIUS * 2.5f;
            float barHeight = 5f;
            float barX = centerX - (barWidth / 2f);
            float barY = centerY + currentRadius + 10f;

            float hpPercent = hp / maxHp;

            shapeRenderer.setColor(0.5f, 0.1f, 0.1f, 1f);
            shapeRenderer.rect(barX, barY, barWidth, barHeight);

            shapeRenderer.setColor(0.2f, 0.6f, 0.9f, 1f);
            shapeRenderer.rect(barX, barY, barWidth * hpPercent, barHeight);

            shapeRenderer.end();

            batch.begin();
        }
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    public void takeDamage(float damage) {
        this.hp -= damage;
        if (this.hp < 0f) this.hp = 0f;
    }

    public void heal(float amount) {
        this.hp += amount;
        if (this.hp > maxHp) this.hp = maxHp;
    }

    public boolean isDead() {
        return this.hp <= 0f;
    }

    // === ГЕТТЕРЫ И СЕТТЕРЫ ===
    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public float getCurrentRadius() { return currentRadius; }
    public float getVx() { return vx; }
    public float getVy() { return vy; }
    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }
    public void setPosition(float x, float y) {
        this.centerX = x;
        this.centerY = y;
    }
}
