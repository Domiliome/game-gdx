package io.github.simple_game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ShopZone {
    // Координаты и размеры прямоугольника зоны продажи
    private final float x;
    private final float y;
    private final float width;
    private final float height;

    // Цвет зоны (полупрозрачный зеленый)
    private final Color zoneColor;

    // Конструктор: инициализирует зону в левом нижнем углу с учетом отступа
    public ShopZone(float padding, float size) {
        this.x = padding;
        this.y = padding;
        this.width = size;
        this.height = size;
        this.zoneColor = new Color(0.3f, 0.6f, 0.3f, 0.4f); // Полупрозрачный зеленый
    }

    // Проверяет, зашел ли салатовый шар в эту зону
    public boolean checkCollision(GreenBall greenBall) {
        // Вычисляем границы зоны продажи
        float shopMaxX = x + width;
        float shopMaxY = y + height;

        // Проверяем, пересекает ли край салатового шара границы зоны
        if (greenBall.getCenterX() - greenBall.getCurrentRadius() < shopMaxX &&
            greenBall.getCenterY() - greenBall.getCurrentRadius() < shopMaxY) {

            // Если столкновение произошло, возвращаем true
            return true;
        }
        return false;
    }

    // Отрисовка зоны продажи с помощью ShapeRenderer
    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(zoneColor);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
    }
}
