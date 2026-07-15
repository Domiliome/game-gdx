package io.github.simple_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GreenBall {
    private float centerX;
    private float centerY;

    private float vx = 0f;
    private float vy = 0f;

    // Трение настроено на плавное затухание в течение ~5 секунд
    private final float FRICTION = 0.985f;
    private final float BOUNCE = 0.9f;

    private final float BASE_RADIUS = 20f;
    private float currentRadius;
    private float targetRadius;
    private final float ANIMATION_SPEED = 10f;

    // ИСПРАВЛЕНО: Лишние переменные bounds, isDragging, offsetX, offsetY, touchPoint полностью удалены!

    private final Viewport viewport;
    private final float padding;

    private final Color ballColor = new Color(0.55f, 0.85f, 0.35f, 1f);

    public GreenBall(float startX, float startY, Viewport viewport, float padding) {
        this.centerX = startX;
        this.centerY = startY;
        this.viewport = viewport;
        this.padding = padding;

        this.currentRadius = BASE_RADIUS;
        this.targetRadius = BASE_RADIUS;

        // ИСПРАВЛЕНО: Удалена инициализация bounds и touchPoint из конструктора

        // На старте шары абсолютно статичны
        this.vx = 0f;
        this.vy = 0f;
    }

    public void update(float step) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        currentRadius = MathUtils.lerp(currentRadius, targetRadius, ANIMATION_SPEED * deltaTime);
        targetRadius = BASE_RADIUS;

        // Физика качения по инерции
        centerX += vx * step;
        centerY += vy * step;

        // Плавное торможение
        vx *= Math.pow(FRICTION, step * 60);
        vy *= Math.pow(FRICTION, step * 60);

        // Полная остановка при микро-скоростях
        if (Math.abs(vx) < 5f) vx = 0f;
        if (Math.abs(vy) < 5f) vy = 0f;

        // Отскоки от стен по горизонтали
        float minX = padding + currentRadius;
        float maxX = viewport.getWorldWidth() - padding - currentRadius;
        if (centerX < minX) { centerX = minX; vx = -vx * BOUNCE; }
        else if (centerX > maxX) { centerX = maxX; vx = -vx * BOUNCE; }

        // Отскоки от стен по вертикали
        float minY = padding + currentRadius;
        float maxY = viewport.getWorldHeight() - padding - currentRadius;
        if (centerY < minY) { centerY = minY; vy = -vy * BOUNCE; }
        else if (centerY > maxY) { centerY = maxY; vy = -vy * BOUNCE; }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(ballColor);
        shapeRenderer.circle(centerX, centerY, currentRadius);
        shapeRenderer.end();
    }

    // Метод оставлен пустым, чтобы Main.java не ругался на отсутствие метода
    public void resetDrag() {
        // Логика удалена за ненадобностью
    }

    // === МЕТОДЫ ДЛЯ ВЗАИМОДЕЙСТВИЯ (ГЕТТЕРЫ И СЕТТЕРЫ) ===
    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public float getCurrentRadius() { return currentRadius; }
    public float getVx() { return vx; }
    public float getVy() { return vy; }
    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }
    public void setPosition(float x, float y) { this.centerX = x; this.centerY = y; }
}
