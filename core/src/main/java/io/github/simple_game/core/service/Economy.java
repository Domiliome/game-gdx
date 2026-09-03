package io.github.simple_game.core.service;

/**
 * Баланс сессии. Старт хватает на одну дешёвую башню или пару тесл;
 * пушка и маг — после первой волны. Доход с 20 волн не закрывает всю карту.
 */
public final class Economy {
    public static final int STARTING_GOLD = 180;
    public static final int STARTING_LIVES = 12;
    public static final int SHOP_REFRESH_COST = 40;

    private Economy() {}

    public static int waveClearBonus(int waveNumber) {
        if (waveNumber <= 0) {
            return 0;
        }
        return 8 + waveNumber * 2;
    }
}
