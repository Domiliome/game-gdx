package io.github.simple_game.core.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.simple_game.core.presentation.view.GameInterface;
import io.github.simple_game.core.presentation.view.GameRenderer;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InteractionService;

public class GameScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private GameLoop gameLoop;
    private GameRenderer gameRenderer;
    private GameInterface gameInterface;
    private InteractionService interactionService;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        gameLoop = new GameLoop();
        gameRenderer = new GameRenderer(gameLoop, camera);
        gameInterface = new GameInterface(gameLoop, camera);

        interactionService = new InteractionService(gameLoop, camera);
        Gdx.input.setInputProcessor(interactionService);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameLoop.update(delta);
        camera.update();

        // Сначала рисуем карту, башни и врагов
        gameRenderer.render();

        // Поверх игрового мира рисуем текст интерфейса
        gameInterface.render();
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
        gameInterface.dispose();
    }
}
