package io.github.simple_game.core.service;

import io.github.simple_game.core.model.entity.tower.TowerType;

/**
 * Сервис управления магазином.
 * Хранит доступный ассортимент башен для игрового процесса.
 */
public class ShopService {

    public ShopService() {
        // Конструктор пуст, так как Scene2D берет TowerType.values() напрямую
    }

    /**
     * Возвращает список всех доступных типов башен в игре.
     */
    public TowerType[] getAvailableTowers() {
        return TowerType.values();
    }
}
