package io.github.simple_game.core.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.simple_game.core.presentation.view.GameInterface;
import io.github.simple_game.core.presentation.view.GameRenderer;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InteractionService;

/**
 * Класс игрового экрана, управляющий жизненным циклом и адаптивным рендерингом основного игрового процесса.
 * Связывает воедино центральную логику обновления мира ({@link GameLoop}) и систему адаптивного
 * масштабирования ({@link Viewport}) под любые экраны смартфонов.
 */
public class GameScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private Viewport viewport; // Добавляем адаптивный слой масштабирования

    private GameLoop gameLoop;
    private GameRenderer gameRenderer;
    private GameInterface gameInterface;
    private InteractionService interactionService;

    @Override
    public void show() {
        camera = new OrthographicCamera();

        // Инициализируем FitViewport с фиксированным виртуальным пиксельным разрешением 480x800
        viewport = new FitViewport(480, 800, camera);
        viewport.apply(true); // Применяем настройки и центрируем камеру в мире

        gameLoop = new GameLoop();
        interactionService = new InteractionService(gameLoop, camera);

        // Передаем viewport в рендерер и интерфейс, чтобы они знали актуальные размеры
        gameRenderer = new GameRenderer(gameLoop, camera, interactionService);
        gameInterface = new GameInterface(gameLoop, camera);

        com.badlogic.gdx.input.GestureDetector gestureDetector = new com.badlogic.gdx.input.GestureDetector(interactionService);
        Gdx.input.setInputProcessor(gestureDetector);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameLoop.update(delta);

        if (interactionService != null) {
            interactionService.updateInertia(delta);
        }

        camera.update();

        // Сначала рисуем карту, башни и врагов
        gameRenderer.render();

        // Поверх игрового мира рисуем текст интерфейса
        gameInterface.render();
    }

    /**
     * Важнейший метод для мобильной адаптивности.
     * Передает новые физические размеры экрана в Viewport для пересчета пропорций без искажений пикселей.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true принудительно центрирует камеру после изменения размеров
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
        gameInterface.dispose();
    }
}
