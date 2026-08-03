package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.simple_game.core.Main;
import io.github.simple_game.core.presentation.screen.GameScreen;
import io.github.simple_game.core.presentation.ui.GameOverWindow;
import io.github.simple_game.core.presentation.ui.ShopPanel;
import io.github.simple_game.core.presentation.ui.TopStatusBar;
import io.github.simple_game.core.presentation.ui.TowerControlPanel;
import io.github.simple_game.core.presentation.ui.VictoryWindow;
import io.github.simple_game.core.service.DragAndDropManager;
import io.github.simple_game.core.service.GameLoop;

public class GameInterface {
    private final Stage stage;
    private final TopStatusBar statusBar;

    // Добавлен параметр Runnable restartAction для сброса сессии окнами конца игры
    public GameInterface(GameLoop gameLoop, Viewport uiViewport, GameRenderer renderer,
                         DragAndDropManager dragManager, Main game, GameScreen gameScreen, Runnable restartAction) {
        this.stage = new Stage(uiViewport);

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        this.statusBar = new TopStatusBar(gameLoop, game, gameScreen);
        ShopPanel shopPanel = new ShopPanel(gameLoop, renderer, dragManager);
        TowerControlPanel upgradeButton = new TowerControlPanel(gameLoop);

        // Создаем оба окна конца игры, передавая им одну и ту же логику перезапуска
        GameOverWindow gameOverWindow = new GameOverWindow(gameLoop, restartAction);
        VictoryWindow victoryWindow = new VictoryWindow(gameLoop, restartAction);

        rootTable.add(statusBar).expandX().left().top().pad(20);
        rootTable.row();
        rootTable.add(upgradeButton).expand().bottom().padBottom(20);
        rootTable.row();
        rootTable.add(shopPanel).expandX().fillX().bottom();

        this.stage.addActor(rootTable);

        // Добавляем окна оверлеями на весь экран поверх основной верстки интерфейса
        gameOverWindow.setFillParent(true);
        this.stage.addActor(gameOverWindow);

        victoryWindow.setFillParent(true);
        this.stage.addActor(victoryWindow);
    }

    public void render() {
        stage.act();
        stage.draw();
    }

    public Stage getStage() { return stage; }
    public void dispose() { stage.dispose(); }
}
