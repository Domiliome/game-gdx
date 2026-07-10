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

    private final float BASE_RADIUS = 32f;
    private float currentRadius;
    private float targetRadius;
    private final float ANIMATION_SPEED = 10f;

    private final Rectangle bounds;
    private boolean isDragging;
    private float offsetX;
    private float offsetY;
    private final Vector3 touchPoint;

    // ДОБАВЛЕНО: Шарик теперь сам хранит ссылки на вьюпорт экрана и размер отступа
    private final Viewport viewport;
    private final float padding;

    // ИЗМЕНЕНО: Конструктор теперь принимает viewport и padding при создании
    public RedBall(float startX, float startY, Viewport viewport, float padding) {
        this.centerX = startX;
        this.centerY = startY;
        this.viewport = viewport; // Запоминаем вьюпорт
        this.padding = padding;   // Запоминаем отступ

        this.currentRadius = BASE_RADIUS;
        this.targetRadius = BASE_RADIUS;
        this.bounds = new Rectangle(0, 0, BASE_RADIUS * 2f, BASE_RADIUS * 2f);
        this.isDragging = false;
        this.touchPoint = new Vector3();
    }


    public void update(float step) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        currentRadius = MathUtils.lerp(currentRadius, targetRadius, ANIMATION_SPEED * deltaTime);

        // Проверяем, касается ли палец экрана вообще
        if (Gdx.input.isTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPoint);

            if (!isDragging) {
                // Вычисляем хитбокс
                bounds.set(centerX - currentRadius, centerY - currentRadius, currentRadius * 2f, currentRadius * 2f);

                // Если попали точно по шару — включаем таскание
                if (bounds.contains(touchPoint.x, touchPoint.y)) {
                    isDragging = true;
                    offsetX = touchPoint.x - centerX;
                    offsetY = touchPoint.y - centerY;
                    vx = 0f;
                    vy = 0f;
                }
            }
        } else {
            // Если экран не трогают — выключаем драг
            if (isDragging) {
                isDragging = false;
            }
        }

        if (isDragging) {
            // Если мы УСПЕШНО ТАЩИМ шар:
            targetRadius = BASE_RADIUS * 1.7f; // Увеличиваем размер

            float oldCenterX = centerX;
            float oldCenterY = centerY;

            centerX = MathUtils.clamp(touchPoint.x - offsetX, padding + currentRadius, viewport.getWorldWidth() - padding - currentRadius);
            centerY = MathUtils.clamp(touchPoint.y - offsetY, padding + currentRadius, viewport.getWorldHeight() - padding - currentRadius);

            vx = (centerX - oldCenterX) / step;
            vy = (centerY - oldCenterY) / step;
        } else {
            // ЕСЛИ ПАЛЕЦ О ТПУЩЕН ИЛИ НАЖАТ МИМО ШАРА:
            // Физика свободного качения ДОЛЖНА продолжать работать!
            targetRadius = BASE_RADIUS; // Сдуваем обратно до нормы

            centerX += vx * step;
            centerY += vy * step;

            // Трение
            vx *= Math.pow(FRICTION, step * 60);
            vy *= Math.pow(FRICTION, step * 60);

            // Отскоки от стен рамки поля
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
        shapeRenderer.setColor(0.9f, 0.3f, 0.3f, 1f);
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
