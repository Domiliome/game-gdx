package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.simple_game.core.Main;
import io.github.simple_game.core.presentation.screen.GameScreen;
import io.github.simple_game.core.presentation.ui.ShopPanel;
import io.github.simple_game.core.presentation.ui.TopStatusBar;
import io.github.simple_game.core.presentation.ui.TowerControlPanel;
import io.github.simple_game.core.service.DragAndDropManager;
import io.github.simple_game.core.service.GameLoop;

public class GameInterface {
    private final Stage stage;
    private final TopStatusBar statusBar;

    public GameInterface(GameLoop gameLoop, Viewport uiViewport, GameRenderer renderer,
                         DragAndDropManager dragManager, Main game, GameScreen gameScreen) {
        this.stage = new Stage(uiViewport);

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        // Передаем Main и GameScreen в конструктор статус-бара для переключения на экран рюкзака
        this.statusBar = new TopStatusBar(gameLoop, game, gameScreen);
        ShopPanel shopPanel = new ShopPanel(gameLoop, renderer, dragManager);
        TowerControlPanel upgradeButton = new TowerControlPanel(gameLoop);

        rootTable.add(statusBar).expandX().left().top().pad(20);
        rootTable.row();
        rootTable.add(upgradeButton).expand().bottom().padBottom(20);
        rootTable.row();
        rootTable.add(shopPanel).expandX().fillX().bottom();

        this.stage.addActor(rootTable);
    }

    public void render() {
        stage.act();
        stage.draw();
    }

    public Stage getStage() { return stage; }
    public void dispose() { stage.dispose(); }
}
