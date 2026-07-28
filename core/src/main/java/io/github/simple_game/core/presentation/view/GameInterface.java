package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.simple_game.core.presentation.ui.ShopPanel;
import io.github.simple_game.core.presentation.ui.TopStatusBar;
import io.github.simple_game.core.presentation.ui.UpgradeButton;
import io.github.simple_game.core.service.DragAndDropManager;
import io.github.simple_game.core.service.GameLoop;

public class GameInterface {
    private final Stage stage;
    private final TopStatusBar statusBar; // <--- ДОБАВЬТЕ ЭТУ СТРОКУ СЮДА!

    public GameInterface(GameLoop gameLoop, Viewport uiViewport, GameRenderer renderer, DragAndDropManager dragManager) {
        this.stage = new Stage(uiViewport);

        Table rootTable = new Table();
        rootTable.setFillParent(true);

        // Инициализируем поле класса
        this.statusBar = new TopStatusBar(gameLoop);
        ShopPanel shopPanel = new ShopPanel(gameLoop, renderer, dragManager);
        UpgradeButton upgradeButton = new UpgradeButton(gameLoop);

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
