package io.github.simple_game.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.github.simple_game.core.model.entity.tower.TowerType;

/**
 * Сервис магазина: 3 случайных разных башни, обновление после покупки или за 20G.
 */
public class ShopService {

    public static final int SLOT_COUNT = 3;
    public static final int REFRESH_COST = 20;

    private final Random random = new Random();
    private final TowerType[] currentSlots = new TowerType[SLOT_COUNT];

    public ShopService() {
        refreshShop();
    }

    public TowerType[] getShopSlots() {
        return currentSlots.clone();
    }

    public void refreshShop() {
        List<TowerType> pool = new ArrayList<>(List.of(TowerType.values()));
        Collections.shuffle(pool, random);
        for (int i = 0; i < SLOT_COUNT; i++) {
            currentSlots[i] = pool.get(i);
        }
    }
}
