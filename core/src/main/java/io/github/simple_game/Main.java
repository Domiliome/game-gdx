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

    private RedBall redBall;
    private GreenBall greenBall;
    private ShopZone shopZone;
    private int money = 0;

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

        redBall = new RedBall(WORLD_WIDTH / 2f - 80f, WORLD_HEIGHT / 2f, viewport, PADDING);
        greenBall = new GreenBall(WORLD_WIDTH / 2f + 80f, WORLD_HEIGHT / 2f, viewport, PADDING);
        shopZone = new ShopZone(PADDING, 80f);

        font = new BitmapFont();
        font.setColor(1f, 1f, 1f, 1f);

        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1.5f);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (deltaTime > 0.25f) deltaTime = 0.25f;

        // --- ФИКСИРОВАННЫЙ ШАГ ФИЗИКИ ---
        physicsAccumulator += deltaTime;
        while (physicsAccumulator >= TIME_STEP) {
            redBall.update(TIME_STEP);
            greenBall.update(TIME_STEP);
            checkBallCollision();

            // ИСПРАВЛЕНО: Предотвращаем бесконечное начисление очков за один пролет
            if (shopZone.checkCollision(greenBall)) {
                // Проверяем, что шар действительно внизу, а не только что телепортировался вверх
                if (greenBall.getCenterY() < viewport.getWorldHeight() / 3f) {
                    money += 10;
                    Gdx.app.log("GAME", "Green ball sold! Current balance: " + money);

                    float startX = viewport.getWorldWidth() / 2f + 80f;
                    float startY = viewport.getWorldHeight() - PADDING - 100f;

                    // Мгновенный перенос и сброс скоростей до следующего тика цикла while
                    greenBall.setPosition(startX, startY);
                    greenBall.setVx(0f);
                    greenBall.setVy(0f);
                }
            }

            physicsAccumulator -= TIME_STEP;
        }

        // --- ОТРИСОВКА ГРАФИКИ ---
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        camera.update();

        // 1. Отрисовка геометрии
        shapeRenderer.setProjectionMatrix(camera.combined);
        shopZone.draw(shapeRenderer);
        redBall.draw(shapeRenderer);
        greenBall.draw(shapeRenderer);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(PADDING, PADDING, viewport.getWorldWidth() - (PADDING * 2f), viewport.getWorldHeight() - (PADDING * 2f));
        shapeRenderer.setColor(1f, 1f, 1f, 0.3f);
        float lineY = viewport.getWorldHeight() / 3f;
        shapeRenderer.line(PADDING, lineY, viewport.getWorldWidth() - PADDING, lineY);
        shapeRenderer.end();

        // 2. Отрисовка текста через SpriteBatch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float textX = PADDING + 20f;
        float textY = viewport.getWorldHeight() - PADDING - 20f;

        font.draw(batch, "Coins: " + money, textX, textY);

        batch.end();
    }

    private void checkBallCollision() {
        float dx = greenBall.getCenterX() - redBall.getCenterX();
        float dy = greenBall.getCenterY() - redBall.getCenterY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float minDistance = redBall.getCurrentRadius() + greenBall.getCurrentRadius();

        if (distance < minDistance) {
            if (distance == 0) return;

            float nx = dx / distance;
            float ny = dy / distance;
            float overlap = minDistance - distance;

            redBall.setPosition(redBall.getCenterX() - nx * overlap * 0.5f, redBall.getCenterY() - ny * overlap * 0.5f);
            greenBall.setPosition(greenBall.getCenterX() + nx * overlap * 0.5f, greenBall.getCenterY() + ny * overlap * 0.5f);

            float rvx = greenBall.getVx() - redBall.getVx();
            float rvy = greenBall.getVy() - redBall.getVy();
            float velAlongNormal = rvx * nx + rvy * ny;

            if (velAlongNormal < 0) {
                float restitution = 0.8f;
                float impulseScalar = -(1 + restitution) * velAlongNormal;
                float redMass = redBall.getCurrentRadius();
                float greenMass = greenBall.getCurrentRadius();
                float totalMass = redMass + greenMass; // ИСПРАВЛЕН
                float impulseX = (impulseScalar * nx) / totalMass;
                float impulseY = (impulseScalar * ny) / totalMass;

                redBall.setVx(redBall.getVx() - greenMass * impulseX);
                redBall.setVy(redBall.getVy() - greenMass * impulseY);
                greenBall.setVx(greenBall.getVx() + redMass * impulseX);
                greenBall.setVy(greenBall.getVy() + redMass * impulseY);
            }
        }
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
