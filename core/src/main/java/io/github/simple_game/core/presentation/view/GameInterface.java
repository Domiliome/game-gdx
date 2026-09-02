package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.simple_game.core.presentation.ui.GameOverWindow;
import io.github.simple_game.core.presentation.ui.ShopPanel;
import io.github.simple_game.core.presentation.ui.TopStatusBar;
import io.github.simple_game.core.presentation.ui.TowerControlPanel;
import io.github.simple_game.core.presentation.ui.VictoryWindow;
import io.github.simple_game.core.presentation.input.DragAndDropManager;
import io.github.simple_game.core.service.GameLoop;

public class GameInterface {
    private final Stage stage;
    private final Table rootTable;
    private final TopStatusBar statusBar;
    private final ShopPanel shopPanel;

    public GameInterface(GameLoop gameLoop, Viewport uiViewport, GameRenderer renderer, DragAndDropManager dragManager,
                         Runnable openInventory, Runnable restartAction, Runnable exitToMenuAction) {
        this.stage = new Stage(uiViewport);

        this.rootTable = new Table();
        rootTable.setFillParent(true);

        this.statusBar = new TopStatusBar(gameLoop, openInventory);
        this.shopPanel = new ShopPanel(gameLoop, renderer, dragManager);
        TowerControlPanel upgradeButton = new TowerControlPanel(gameLoop);


        GameOverWindow gameOverWindow = new GameOverWindow(gameLoop, restartAction);
        VictoryWindow victoryWindow = new VictoryWindow(gameLoop, restartAction, exitToMenuAction);

        rootTable.add(statusBar).expandX().fillX().top();
        rootTable.row();
        rootTable.add(upgradeButton).expand().bottom().padBottom(8);
        rootTable.row();
        rootTable.add(shopPanel).expandX().fillX().bottom();

        this.stage.addActor(rootTable);

        gameOverWindow.setFillParent(true);
        this.stage.addActor(gameOverWindow);

        victoryWindow.setFillParent(true);
        this.stage.addActor(victoryWindow);
    }

    public void render() {
        stage.act();
        stage.draw();
    }

    public void validateLayout() {
        stage.getRoot().setSize(stage.getWidth(), stage.getHeight());
        rootTable.invalidateHierarchy();
        rootTable.validate();
    }

    public float getTopInset() {
        return Math.max(statusBar.getHeight(), statusBar.getPrefHeight());
    }

    public float getBottomInset() {
        return Math.max(shopPanel.getHeight(), shopPanel.getPrefHeight());
    }

    public Stage getStage() { return stage; }
    public void dispose() { stage.dispose(); }
}
