package io.github.simple_game.core.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.simple_game.core.presentation.view.GameInterface;
import io.github.simple_game.core.presentation.view.GameRenderer;
import io.github.simple_game.core.service.CameraGestureService;
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
    private CameraGestureService cameraGestureService;

    @Override
    public void show() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(480, 800, worldCamera);
        uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);

        gameLoop = new GameLoop();
        interactionService = new InteractionService(gameLoop, worldViewport);
        cameraGestureService = new CameraGestureService(worldViewport);
        gameRenderer = new GameRenderer(gameLoop, worldCamera, interactionService);

        gameInterface = new GameInterface(
            gameLoop, uiViewport, gameRenderer, interactionService.getDragAndDropManager()
        );

        initInputProcessing();
    }

    private void initInputProcessing() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gameInterface.getStage());
        multiplexer.addProcessor(new GestureDetector(cameraGestureService));
        multiplexer.addProcessor(new GestureDetector(interactionService));
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameLoop.update(delta);
        if (cameraGestureService != null) {
            cameraGestureService.updateInertia(delta);
        }

        worldViewport.apply();
        worldCamera.update();
        gameRenderer.render();

        uiViewport.apply();
        uiCamera.update();
        gameInterface.render();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, false);

        float worldW = worldViewport.getWorldWidth();
        float worldH = worldViewport.getWorldHeight();

        worldCamera.position.set(worldW / 2f, worldH / 2f, 0);
        worldCamera.update();

        rebuildDynamicPath(worldH);

        uiViewport.update(width, height, true);
    }

    /**
     * ИСПРАВЛЕНО: Генерирует ровно 6 ключевых точек вейпоинтов для плавного бега врагов.
     * Координаты подогнаны строго под шаг сетки 32 с центровкой в +16 пикселей.
     */
    private void rebuildDynamicPath(float worldHeight) {
        io.github.simple_game.core.model.movement.RoadPath path = gameLoop.getRoadPath();
        path.clear();

        float startY = com.badlogic.gdx.math.MathUtils.floor((worldHeight + 32f) / 32f) * 32f + 16f;

        path.addPoint(240f, startY);
        path.addPoint(240f, 528f);
        path.addPoint(48f, 528f);
        path.addPoint(48f, 176f);
        path.addPoint(432f, 176f);
        path.addPoint(432f, -48f);
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
        gameInterface.dispose();
    }
}
