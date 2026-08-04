package io.github.simple_game.core;

import com.badlogic.gdx.Game;

import io.github.simple_game.core.presentation.screen.MainMenuScreen;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InventoryManager;

public class Main extends Game {
    private InventoryManager globalInventory;
    private GameLoop temporaryLoop;

    @Override
    public void create() {
        this.globalInventory = new InventoryManager();
        this.temporaryLoop = new GameLoop(this);
        setScreen(new MainMenuScreen(this));
    }
    public InventoryManager getGlobalInventory() { return globalInventory; }
    public GameLoop getGlobalInventoryGameLoop() { return temporaryLoop; }
}
