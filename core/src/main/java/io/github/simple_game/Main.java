package io.github.simple_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;

    private GameManager gameManager;
    private BitmapFont font;

    private final float WORLD_WIDTH = 480f;
    private final float WORLD_HEIGHT = 800f;
    private final float PADDING = 20f;

    private float physicsAccumulator = 0f;
    private final float TIME_STEP = 1f / 60f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // Инициализируем менеджер игры, который создаст все объекты внутри себя
        gameManager = new GameManager(viewport, PADDING, WORLD_WIDTH, WORLD_HEIGHT);

        font = new BitmapFont();
        font.setColor(1f, 1f, 1f, 1f);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1.5f);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (deltaTime > 0.25f) deltaTime = 0.25f;

        // Фиксированный шаг физики переделываем под работу с менеджером
        physicsAccumulator += deltaTime;
        while (physicsAccumulator >= TIME_STEP) {
            gameManager.updatePhysics(TIME_STEP);
            physicsAccumulator -= TIME_STEP;
        }

        // Отрисовка сцены
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        gameManager.getShopZone().draw(shapeRenderer);
        gameManager.getRedBall().draw(shapeRenderer);

        for (GreenBall gb : gameManager.getGreenBalls()) {
            gb.draw(shapeRenderer);
        }

        // Отрисовка границ игрового поля
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(PADDING, PADDING, viewport.getWorldWidth() - (PADDING * 2f), viewport.getWorldHeight() - (PADDING * 2f));

        // Визуальная линия на уровне 40% экрана
        shapeRenderer.setColor(1f, 1f, 1f, 0.3f);
        float lineY = viewport.getWorldHeight() * 0.4f;
        shapeRenderer.line(PADDING, lineY, viewport.getWorldWidth() - PADDING, lineY);
        shapeRenderer.end();

        // Отрисовка текста (интерфейса)
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        float textX = PADDING + 20f;
        float textY = viewport.getWorldHeight() - PADDING - 20f;
        font.draw(batch, "Coins: " + gameManager.getMoney(), textX, textY);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (font != null) font.dispose();
    }
}
