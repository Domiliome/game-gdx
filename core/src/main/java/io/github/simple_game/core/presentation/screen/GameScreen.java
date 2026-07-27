package io.github.simple_game.core.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.input.GestureDetector;

import io.github.simple_game.core.presentation.view.GameInterface;
import io.github.simple_game.core.presentation.view.GameRenderer;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InteractionService;

public class GameScreen extends ScreenAdapter {
    private OrthographicCamera worldCamera;
    private Viewport worldViewport;
    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private GameLoop gameLoop;
    private GameRenderer gameRenderer;
    private GameInterface gameInterface;
    private InteractionService interactionService;

    @Override
    public void show() {
        worldCamera = new OrthographicCamera();
        worldViewport = new FitViewport(480, 800, worldCamera);
        uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);

        gameLoop = new GameLoop();
        interactionService = new InteractionService(gameLoop, worldViewport);
        gameRenderer = new GameRenderer(gameLoop, worldCamera, interactionService);

        // Исправлено: передаем все 4 необходимые зависимости для модульного Scene2D
        gameInterface = new GameInterface(
            gameLoop,
            uiViewport,
            gameRenderer,
            interactionService.getDragAndDropManager()
        );

        initInputProcessing();
    }

    private void initInputProcessing() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        // Первым делом клики и свайпы забирает Scene2D интерфейс (ShopPanel)
        multiplexer.addProcessor(gameInterface.getStage());
        // Вторым делом — жесты камеры и тапы по сетке игрового мира
        multiplexer.addProcessor(new GestureDetector(interactionService));
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameLoop.update(delta);
        if (interactionService != null) {
            interactionService.updateInertia(delta);
        }

        // РЕНДЕР СЛОЯ 1: Игровой мир
        worldViewport.apply();
        worldCamera.update();
        gameRenderer.render();

        // РЕНДЕР СЛОЯ 2: Интерфейс (UI)
        uiViewport.apply();
        uiCamera.update();
        gameInterface.render();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
        gameInterface.dispose();
    }
}
