package io.github.simple_game.core.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.simple_game.core.Main;
import io.github.simple_game.core.model.movement.PathType;
import io.github.simple_game.core.presentation.view.GameInterface;
import io.github.simple_game.core.presentation.view.GameRenderer;
import io.github.simple_game.core.presentation.GameViewport;
import io.github.simple_game.core.model.entity.map.GameGrid;
import io.github.simple_game.core.service.CameraGestureService;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InteractionService;
import io.github.simple_game.core.model.movement.PathGenerator;

public class GameScreen extends ScreenAdapter {
    private final Main game;
    private OrthographicCamera worldCamera;
    private Viewport worldViewport;
    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private GameLoop gameLoop;
    private GameRenderer gameRenderer;
    private GameInterface gameInterface;
    private InteractionService interactionService;
    private CameraGestureService cameraGestureService;

    private PathType activeMapType;

    public GameScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        if (gameLoop == null) {
            worldCamera = new OrthographicCamera();
            worldViewport = new FitViewport(GameGrid.worldWidth(), GameGrid.worldHeight(), worldCamera);
            uiCamera = new OrthographicCamera();
            uiViewport = new ExtendViewport(GameViewport.WIDTH, GameViewport.HEIGHT, uiCamera);

            gameLoop = new GameLoop(game);

            interactionService = new InteractionService(gameLoop, worldViewport);
            cameraGestureService = new CameraGestureService(worldViewport);
            gameRenderer = new GameRenderer(gameLoop, worldCamera, interactionService);

            gameInterface = new GameInterface(
                gameLoop, uiViewport, gameRenderer,
                interactionService.getDragAndDropManager(), game, this,
                () -> {
                    gameLoop = null;
                    show();
                    resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                },
                () -> game.setScreen(new MainMenuScreen(game))
            );

            PathType[] types = PathType.values();
            this.activeMapType = types[com.badlogic.gdx.math.MathUtils.random(0, types.length - 1)];
            PathGenerator.generate(gameLoop.getRoadPath(), activeMapType);
        }

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

        boolean isGameOver = gameLoop.getCurrencyManager().getLives() <= 0;
        boolean isVictory = gameLoop.isVictory();

        if (!isGameOver && !isVictory) {
            gameLoop.update(delta);
            if (cameraGestureService != null) {
                cameraGestureService.updateInertia(delta);
            }
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
        uiViewport.update(width, height, true);
        gameInterface.validateLayout();

        float uiWorldH = uiViewport.getWorldHeight();
        int uiScreenX = uiViewport.getScreenX();
        int uiScreenY = uiViewport.getScreenY();
        int uiScreenW = uiViewport.getScreenWidth();
        int uiScreenH = uiViewport.getScreenHeight();

        int topPx = Math.round(gameInterface.getTopInset() / uiWorldH * uiScreenH);
        int bottomPx = Math.round(gameInterface.getBottomInset() / uiWorldH * uiScreenH);
        int playH = Math.max(1, uiScreenH - topPx - bottomPx);

        worldViewport.update(uiScreenW, playH, false);
        worldViewport.setScreenPosition(
                uiScreenX + worldViewport.getScreenX(),
                uiScreenY + bottomPx + worldViewport.getScreenY()
        );

        worldCamera.position.set(GameGrid.worldWidth() / 2f, GameGrid.worldHeight() / 2f, 0);
        if (cameraGestureService != null) {
            cameraGestureService.clampToWorld();
        }
        worldCamera.update();
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
        gameInterface.dispose();
    }
}
