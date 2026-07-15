package io.github.simple_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class ShopZone {
    private final float x;
    private final float y;
    private final float width;
    private final float height;
    private final Color zoneColor;

    public ShopZone(float padding, float size) {
        this.x = padding;
        this.y = padding;
        this.width = size;
        this.height = size;
        this.zoneColor = new Color(0.3f, 0.6f, 0.3f, 0.4f);
    }

    public boolean checkCollision(GreenBall greenBall) {
        float ballX = greenBall.getCenterX();
        float ballY = greenBall.getCenterY();
        float radius = greenBall.getCurrentRadius();

        float closestX = MathUtils.clamp(ballX, x, x + width);
        float closestY = MathUtils.clamp(ballY, y, y + height);

        float distanceX = ballX - closestX;
        float distanceY = ballY - closestY;

        float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        return distanceSquared < (radius * radius);
    }

    // ИСПРАВЛЕНО: Безопасное включение прозрачности через встроенные функции LibGDX
    public void draw(ShapeRenderer shapeRenderer) {
        // Заставляем ShapeRenderer использовать правильное смешивание цветов
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Включаем встроенный блендинг для поддержки альфа-канала (0.4f)
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

        shapeRenderer.setColor(zoneColor);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        // Обязательно выключаем, чтобы не ломать отрисовку линий и интерфейса
        Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
    }
}
