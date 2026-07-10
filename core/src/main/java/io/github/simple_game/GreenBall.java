package io.github.simple_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GreenBall {
    private float centerX;
    private float centerY;

    private float vx = 0f;
    private float vy = 0f;
    private final float FRICTION = 0.97f;

    // ЖЕСТКАЯ НАСТРОЙКА: Салатовый шар очень прыгучий (теряет всего 10% скорости)
    private final float BOUNCE = 0.9f;

    // ЖЕСТКАЯ НАСТРОЙКА: Радиус меньше, чем у красного (20 пикселей)
    private final float BASE_RADIUS = 20f;
    private float currentRadius;
    private float targetRadius;
    private final float ANIMATION_SPEED = 10f;

    private final Rectangle bounds;
    private boolean isDragging;
    private float offsetX;
    private float offsetY;
    private final Vector3 touchPoint;

    private final Viewport viewport;
    private final float padding;

    // ЖЕСТКАЯ НАСТРОЙКА: Не слишком яркий салатовый цвет
    private final Color ballColor = new Color(0.55f, 0.85f, 0.35f, 1f);

    public GreenBall(float startX, float startY, Viewport viewport, float padding) {
        this.centerX = startX;
        this.centerY = startY;
        this.viewport = viewport;
        this.padding = padding;

        this.currentRadius = BASE_RADIUS;
        this.targetRadius = BASE_RADIUS;
        this.bounds = new Rectangle(0, 0, BASE_RADIUS * 2f, BASE_RADIUS * 2f);
        this.isDragging = false;
        this.touchPoint = new Vector3();
    }

    public void update(float step) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        currentRadius = MathUtils.lerp(currentRadius, targetRadius, ANIMATION_SPEED * deltaTime);

        if (Gdx.input.isTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPoint);

            if (!isDragging) {
                bounds.set(centerX - currentRadius, centerY - currentRadius, currentRadius * 2f, currentRadius * 2f);

                if (bounds.contains(touchPoint.x, touchPoint.y)) {
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

            centerX = MathUtils.clamp(touchPoint.x - offsetX, padding + currentRadius, viewport.getWorldWidth() - padding - currentRadius);
            centerY = MathUtils.clamp(touchPoint.y - offsetY, padding + currentRadius, viewport.getWorldHeight() - padding - currentRadius);

            vx = (centerX - oldCenterX) / step;
            vy = (centerY - oldCenterY) / step;
        } else {
            targetRadius = BASE_RADIUS;

            centerX += vx * step;
            centerY += vy * step;

            vx *= Math.pow(FRICTION, step * 60);
            vy *= Math.pow(FRICTION, step * 60);

            float minX = padding + currentRadius;
            float maxX = viewport.getWorldWidth() - padding - currentRadius;
            if (centerX < minX) { centerX = minX; vx = -vx * BOUNCE; }
            else if (centerX > maxX) { centerX = maxX; vx = -vx * BOUNCE; }

            float minY = padding + currentRadius;
            float maxY = viewport.getWorldHeight() - padding - currentRadius;
            if (centerY < minY) { centerY = minY; vy = -vy * BOUNCE; }
            else if (centerY > maxY) { centerY = maxY; vy = -vy * BOUNCE; }
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(ballColor);
        shapeRenderer.circle(centerX, centerY, currentRadius);
        shapeRenderer.end();
    }

    // === МЕТОДЫ ДЛЯ ВЗАИМОДЕЙСТВИЯ (ГЕТТЕРЫ И СЕТТЕРЫ) ===
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
