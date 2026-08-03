package io.github.simple_game.core;

import com.badlogic.gdx.Game;
import io.github.simple_game.core.presentation.screen.GameScreen;
import io.github.simple_game.core.service.InventoryManager; // Наш менеджер рюкзака

public class Main extends Game {
    // ВАЖНО: Делаем инвентарь глобальным полем игрового процесса
    private InventoryManager globalInventory;

    @Override
    public void create() {
        // Инициализируем рюкзак строго ОДИН РАЗ за всё время жизни приложения
        this.globalInventory = new InventoryManager();

        // Передаем экран игры дальше
        setScreen(new GameScreen(this));
    }

    /**
     * @return ссылка на персистентный рюкзак, который никогда не сбрасывается между сессиями
     */
    public InventoryManager getGlobalInventory() {
        return globalInventory;
    }
}
