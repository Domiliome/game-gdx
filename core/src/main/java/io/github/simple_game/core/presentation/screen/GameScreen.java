package io.github.simple_game.core.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import io.github.simple_game.core.presentation.view.GameInterface;
import io.github.simple_game.core.presentation.view.GameRenderer;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InteractionService;


/**
 * Класс игрового экрана, управляющий жизненным циклом и рендерингом основного игрового процесса.
 * Связывает воедино центральную логику обновления мира ({@link GameLoop}),
 * систему отрисовки графики ({@link GameRenderer}), пользовательский текстовый интерфейс ({@link GameInterface})
 * и обработку пользовательского ввода ({@link InteractionService}).
 */
public class GameScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private GameLoop gameLoop;
    private GameRenderer gameRenderer;
    private GameInterface gameInterface;
    private InteractionService interactionService;

    /**
     * Вызывается автоматически LibGDX в момент переключения на этот экран и его активации.
     * Отвечает за инициализацию ортографической камеры с фиксированным виртуальным разрешением,
     * создание основного игрового цикла, подсистем отображения и регистрацию обработчика нажатий.
     */
    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);

        gameLoop = new GameLoop();
        gameRenderer = new GameRenderer(gameLoop, camera);
        gameInterface = new GameInterface(gameLoop, camera);
        interactionService = new InteractionService(gameLoop, camera);
        com.badlogic.gdx.input.GestureDetector gestureDetector = new com.badlogic.gdx.input.GestureDetector(interactionService);
        Gdx.input.setInputProcessor(gestureDetector);
    }


    /**
     * Главный метод отрисовки и логического шага, вызываемый фреймворком каждый кадр.
     * Производит очистку буфера экрана черным цветом, обновляет состояние игрового мира с учетом прошедшего времени,
     * синхронизирует матрицу камеры и последовательно рендерит графические объекты, а поверх них — слой интерфейса.
     *
     * @param delta время, прошедшее с момента отрисовки предыдущего кадра в секундах
     */
 @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 1. Обновляем игровой цикл
        gameLoop.update(delta);

        // 2. Обновляем плавное скольжение камеры после свайпа пальцем
        if (interactionService != null) {
            interactionService.updateInertia(delta);
        }

        camera.update();

        // 3. Отрисовка
        gameRenderer.render();
        gameInterface.render();
    }

    /**
     * Вызывается при закрытии игры или смене экрана для освобождения ресурсов.
     * Гарантирует принудительную очистку памяти графических контекстов рендерера и интерфейса,
     * предотвращая утечки ресурсов на целевых платформах.
     */
    @Override
    public void dispose() {
        gameRenderer.dispose();
        gameInterface.dispose();
    }
}
