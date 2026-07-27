package io.github.simple_game.core.model.entity.map;

import com.badlogic.gdx.math.Rectangle;

import io.github.simple_game.core.model.entity.tower.TowerType;

/**
 * Класс, представляющий отдельную ячейку (слот/кнопку) в магазине башен.
 */
public class ShopSlot {
    private final TowerType towerType;
    private final Rectangle bounds;

    /**
     * Создает слот магазина с жестко заданными координатами.
     */
    public ShopSlot(TowerType towerType, float x, float y, float width, float height) {
        this.towerType = towerType;
        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Проверяет, попал ли клик игрока внутрь границ этой кнопки.
     */
    public boolean isClicked(float worldX, float worldY) {
        return bounds.contains(worldX, worldY);
    }

    public TowerType getTowerType() { return towerType; }
    public Rectangle getBounds() { return bounds; }
}
