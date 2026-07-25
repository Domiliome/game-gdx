package io.github.simple_game.core.service;

import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.ShopSlot;
import io.github.simple_game.core.model.entity.TowerType;

/**
 * Сервис управления магазином. Автоматически распределяет башни по кнопкам
 * и находит, какую именно башню выбрал игрок.
 */
public class ShopService {
    private final Array<ShopSlot> slots;
    public static final float SHOP_HEIGHT = 100f;

    public ShopService() {
        this.slots = new Array<>();
        generateSlots();
    }

    /**
     * Автоматически берет ВСЕ башни из TowerType и выстраивает их в ряд.
     * Если ты добавишь новую башню в enum TowerType, она появится в магазине САМА.
     */
    private void generateSlots() {
        TowerType[] types = TowerType.values();
        float slotWidth = 480f / types.length;

        for (int i = 0; i < types.length; i++) {
            float slotX = i * slotWidth;
            slots.add(new ShopSlot(types[i], slotX, 0, slotWidth, SHOP_HEIGHT));
        }
    }

    /**
     * Сканирует слоты и возвращает тип башни, если игрок попал по кнопке.
     * @return TowerType выбранной башни или null, если клик мимо кнопок.
     */
    public TowerType getSelectedTowerType(float worldX, float worldY) {
        if (worldY > SHOP_HEIGHT) return null;

        for (ShopSlot slot : slots) {
            if (slot.isClicked(worldX, worldY)) {
                return slot.getTowerType();
            }
        }
        return null;
    }

    public Array<ShopSlot> getSlots() { return slots; }
}
