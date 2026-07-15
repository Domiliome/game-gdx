package io.github.simple_game;

import com.badlogic.gdx.Gdx;
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

    private final float GRAVITY = -800f;

    private final float BASE_RADIUS = 32f;
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

    public RedBall(float startX, float startY, Viewport viewport, float padding) {
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

                // ИЗМЕНЕНО: Схватить шар можно только в пределах нижней 40% части экрана
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

            // Вычисляем желаемые координаты, куда пользователь тянет шар
            float desiredX = touchPoint.x - offsetX;
            float desiredY = touchPoint.y - offsetY;

            // Жесткое ограничение по оси X (не даем выйти за левый/правый края)
            centerX = MathUtils.clamp(desiredX, padding + currentRadius, viewport.getWorldWidth() - padding - currentRadius);

            // ИЗМЕНЕНО: Мягкий порог ограничения по оси Y теперь смещен на 40% экрана
            float maxAllowedY = (viewport.getWorldHeight() * 0.4f) - padding - currentRadius;
            float minY = padding + currentRadius;

            if (desiredY <= maxAllowedY) {
                // Если палец в пределах нижней зоны — шар следует за ним идеально
                centerY = MathUtils.clamp(desiredY, minY, maxAllowedY);
            } else {
                // Если палец ушел выше линии 40%, включаем "резиновое натяжение" (lerp)
                float rubberY = MathUtils.lerp(maxAllowedY, desiredY, 0.15f);

                // Не даем резинке растянуться выше, чем на +50 пикселей от линии
                centerY = MathUtils.clamp(rubberY, maxAllowedY, maxAllowedY + 50f);
            }

            // Рассчитываем скорость взмаха
            vx = (centerX - oldCenterX) / step;
            vy = (centerY - oldCenterY) / step;

        } else {
            targetRadius = BASE_RADIUS;

            vy += GRAVITY * step;
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
            if (ballCenterYCheck(minY, maxY)) {

            }
        }
    }

    private boolean ballCenterYCheck(float minY, float maxY) {
        if (centerY < minY) {
            centerY = minY;
            vy = -vy * BOUNCE;
            if (Math.abs(vy) < 20f) vy = 0f;
            return true;
        }
        else if (centerY > maxY) {
            centerY = maxY;
            vy = -vy * BOUNCE;
            return true;
        }
        return false;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.9f, 0.3f, 0.3f, 1f);
        shapeRenderer.circle(centerX, centerY, currentRadius);
        shapeRenderer.end();
    }

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
