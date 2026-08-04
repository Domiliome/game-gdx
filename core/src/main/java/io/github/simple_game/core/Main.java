package io.github.simple_game.core;

import com.badlogic.gdx.Game;
import io.github.simple_game.core.presentation.screen.MainMenuScreen; // Импортируем меню
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InventoryManager;

public class Main extends Game {
    private InventoryManager globalInventory;
    private GameLoop temporaryLoop; // Временный цикл для чтения шмоток из меню

    @Override
    public void create() {
        this.globalInventory = new InventoryManager();
        this.temporaryLoop = new GameLoop(this); // Инициализируем контейнер для инвентаря

        // ИСПРАВЛЕНО: Теперь при старте приложения открывается ГЛАВНОЕ МЕНЮ!
        setScreen(new MainMenuScreen(this));
    }

    public InventoryManager getGlobalInventory() { return globalInventory; }
    public GameLoop getGlobalInventoryGameLoop() { return temporaryLoop; }
}
