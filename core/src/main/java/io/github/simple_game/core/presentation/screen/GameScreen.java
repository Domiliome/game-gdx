package io.github.simple_game.core.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import io.github.simple_game.core.presentation.view.GameRenderer;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InteractionService;

public class GameScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private GameLoop gameLoop;
    private GameRenderer gameRenderer;
    private InteractionService interactionService;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        // Задаем виртуальное разрешение экрана игры
        camera.setToOrtho(false, 800, 480);

        gameLoop = new GameLoop();
        gameRenderer = new GameRenderer(gameLoop, camera);

        // Подключаем клики
        interactionService = new InteractionService(gameLoop, camera);
        Gdx.input.setInputProcessor(interactionService);
    }

    @Override
    public void render(float delta) {
        // Очищаем экран черным цветом перед каждым кадром
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 1. Обновляем логику игры (передаем deltaTime)
        gameLoop.update(delta);

        // 2. Обновляем камеру (если она двигалась, хотя в TD она обычно статична)
        camera.update();

        // 3. Рисуем мир
        gameRenderer.render();
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
    }
}
