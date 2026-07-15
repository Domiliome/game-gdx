package io.github.simple_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
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

    private final Vector3 touchVector = new Vector3();

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

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

        // --- ОБРАБОТКА ТАПОВ ПО МАГАЗИНУ ---
        if (Gdx.input.justTouched()) {
            touchVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchVector);

            float shopTopY = viewport.getWorldHeight() - PADDING;
            float shopBottomY = viewport.getWorldHeight() - 110f;

            if (touchVector.y >= shopBottomY && touchVector.y <= shopTopY) {
                float halfWidth = viewport.getWorldWidth() / 2f;

                if (touchVector.x >= PADDING && touchVector.x < halfWidth) {
                    gameManager.buyDamageUpgrade();
                }
                else if (touchVector.x >= halfWidth && touchVector.x <= viewport.getWorldWidth() - PADDING) {
                    gameManager.buyHpUpgrade();
                }
            }
        }

        // --- ШАГ ФИЗИКИ ---
        physicsAccumulator += deltaTime;
        while (physicsAccumulator >= TIME_STEP) {
            gameManager.updatePhysics(TIME_STEP);
            physicsAccumulator -= TIME_STEP;
        }

        // --- ОТРИСОВКА СЦЕНЫ ---
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // 1. Рисуем ТОЛЬКО картинки (спрайты) всех объектов в одной чистой сессии
        batch.begin();
        if (gameManager.getRedBall() != null) {
            gameManager.getRedBall().draw(batch, shapeRenderer);
        }
        for (GreenBall gb : gameManager.getGreenBalls()) {
            gb.draw(batch, shapeRenderer);
        }
        batch.end();

        // 2. Отрисовка геометрии (границы игрового поля и разделительная линия)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(PADDING, PADDING, viewport.getWorldWidth() - (PADDING * 2f), viewport.getWorldHeight() - (PADDING * 2f));

        shapeRenderer.setColor(1f, 1f, 1f, 0.3f);
        float lineY = viewport.getWorldHeight() * 0.4f;
        shapeRenderer.line(PADDING, lineY, viewport.getWorldWidth() - PADDING, lineY);
        shapeRenderer.end();

        // Отрисовка подложки под кнопки магазина
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.25f, 1f);
        float shopBarHeight = 90f;
        shapeRenderer.rect(PADDING, viewport.getWorldHeight() - PADDING - shopBarHeight, viewport.getWorldWidth() - (PADDING * 2f), shopBarHeight);
        shapeRenderer.end();

        // Отрисовка разделителя между кнопками магазина
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.4f, 0.4f, 0.5f, 1f);
        shapeRenderer.line(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() - PADDING, viewport.getWorldWidth() / 2f, viewport.getWorldHeight() - PADDING - shopBarHeight);
        shapeRenderer.end();

        // --- ОТРИСОВКА ИНТЕРФЕЙСА ТЕКСТА ---
        batch.begin();
        float textX = PADDING + 20f;
        float textY = viewport.getWorldHeight() - PADDING - 15f;
        font.draw(batch, "Coins: " + gameManager.getMoney(), textX, textY);

        float btnY = viewport.getWorldHeight() - PADDING - 50f;
        String dmgText = "ATK Lvl:" + gameManager.getDamageLevel() + " (" + gameManager.getDamageCost() + "g)";
        font.draw(batch, dmgText, PADDING + 15f, btnY);

        String hpText = "HP Lvl:" + gameManager.getHpLevel() + " (" + gameManager.getHpCost() + "g)";
        font.draw(batch, hpText, (viewport.getWorldWidth() / 2f) + 15f, btnY);

        // ДОБАВЛЕНО: Текст текущей волны по центру экрана чуть ниже панели магазина
        String waveText = "WAVE: " + gameManager.getCurrentWave();
        font.draw(batch, waveText, (viewport.getWorldWidth() / 2f) - 45f, viewport.getWorldHeight() - PADDING - shopBarHeight - 25f);

        batch.end();
    }

    @Override
    public void pause() {
        if (gameManager != null) {
            gameManager.saveProgress();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (gameManager != null) {
            gameManager.saveProgress();
            if (gameManager.getRedBall() != null) {
                gameManager.getRedBall().dispose();
            }
            for (GreenBall gb : gameManager.getGreenBalls()) {
                gb.dispose();
            }
        }
        batch.dispose();
        shapeRenderer.dispose();
        if (font != null) font.dispose();
    }
}
