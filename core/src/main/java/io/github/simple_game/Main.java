package io.github.simple_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;

    private RedBall redBall;
    private Array<GreenBall> greenBalls;
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

        redBall = new RedBall(WORLD_WIDTH / 2f, WORLD_HEIGHT / 4f, viewport, PADDING);
        shopZone = new ShopZone(PADDING, 80f);
        greenBalls = new Array<>();

        spawnGreenBalls(3);

        font = new BitmapFont();
        font.setColor(1f, 1f, 1f, 1f);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.getData().setScale(1.5f);
    }

    // ИСПРАВЛЕНО: Теперь минимальная высота спавна привязана к планке 40%
    private void spawnGreenBalls(int count) {
        for (int i = 0; i < count; i++) {
            float randomX = MathUtils.random(PADDING + 30f, WORLD_WIDTH - PADDING - 30f);

            // ИЗМЕНЕНО: Шары рождаются строго выше новой линии 40% экрана
            float minY = (WORLD_HEIGHT * 0.4f) + 50f;
            float randomY = MathUtils.random(minY, WORLD_HEIGHT - PADDING - 50f);

            greenBalls.add(new GreenBall(randomX, randomY, viewport, PADDING));
        }
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (deltaTime > 0.25f) deltaTime = 0.25f;

        // --- ФИКСИРОВАННЫЙ ШАГ ФИЗИКИ ---
        physicsAccumulator += deltaTime;
        while (physicsAccumulator >= TIME_STEP) {
            redBall.update(TIME_STEP);

            for (GreenBall gb : greenBalls) {
                gb.update(TIME_STEP);
            }

            checkBallCollisions();
            checkGreenBallCollisions();
            checkShopZoneCollision();

            if (greenBalls.size <= 1) {
                spawnGreenBalls(2);
            }

            physicsAccumulator -= TIME_STEP;
        }

        // --- ОТРИСОВКА ГРАФИКИ ---
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shopZone.draw(shapeRenderer);
        redBall.draw(shapeRenderer);

        for (GreenBall gb : greenBalls) {
            gb.draw(shapeRenderer);
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(PADDING, PADDING, viewport.getWorldWidth() - (PADDING * 2f), viewport.getWorldHeight() - (PADDING * 2f));

        // ИЗМЕНЕНО: Визуальная линия поднята ровно на уровень 40% экрана
        shapeRenderer.setColor(1f, 1f, 1f, 0.3f);
        float lineY = viewport.getWorldHeight() * 0.4f;
        shapeRenderer.line(PADDING, lineY, viewport.getWorldWidth() - PADDING, lineY);
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        float textX = PADDING + 20f;
        float textY = viewport.getWorldHeight() - PADDING - 20f;
        font.draw(batch, "Coins: " + money, textX, textY);
        batch.end();
    }

    private void checkBallCollisions() {
        for (GreenBall gb : greenBalls) {
            float dx = gb.getCenterX() - redBall.getCenterX();
            float dy = gb.getCenterY() - redBall.getCenterY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float minDistance = redBall.getCurrentRadius() + gb.getCurrentRadius();

            if (distance < minDistance) {
                if (distance == 0) continue;

                float nx = dx / distance;
                float ny = dy / distance;
                float overlap = minDistance - distance;

                redBall.setPosition(redBall.getCenterX() - nx * overlap * 0.5f, redBall.getCenterY() - ny * overlap * 0.5f);
                gb.setPosition(gb.getCenterX() + nx * overlap * 0.5f, gb.getCenterY() + ny * overlap * 0.5f);

                float rvx = gb.getVx() - redBall.getVx();
                float rvy = gb.getVy() - redBall.getVy();
                float velAlongNormal = rvx * nx + rvy * ny;

                if (velAlongNormal < 0) {
                    float restitution = 0.8f;
                    float impulseScalar = -(1 + restitution) * velAlongNormal;
                    float redMass = redBall.getCurrentRadius();
                    float greenMass = gb.getCurrentRadius();
                    float totalMass = redMass + greenMass;
                    float impulseX = (impulseScalar * nx) / totalMass;
                    float impulseY = (impulseScalar * ny) / totalMass;

                    redBall.setVx(redBall.getVx() - greenMass * impulseX);
                    redBall.setVy(redBall.getVy() - greenMass * impulseY);
                    gb.setVx(gb.getVx() + redMass * impulseX);
                    gb.setVy(gb.getVy() + redMass * impulseY);
                }
            }
        }
    }

    private void checkGreenBallCollisions() {
        for (int i = 0; i < greenBalls.size; i++) {
            for (int j = i + 1; j < greenBalls.size; j++) {
                GreenBall b1 = greenBalls.get(i);
                GreenBall b2 = greenBalls.get(j);

                float dx = b2.getCenterX() - b1.getCenterX();
                float dy = b2.getCenterY() - b1.getCenterY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float minDistance = b1.getCurrentRadius() + b2.getCurrentRadius();

                if (distance < minDistance) {
                    if (distance == 0) continue;

                    float nx = dx / distance;
                    float ny = dy / distance;
                    float overlap = minDistance - distance;

                    b1.setPosition(b1.getCenterX() - nx * overlap * 0.5f, b1.getCenterY() - ny * overlap * 0.5f);
                    b2.setPosition(b2.getCenterX() + nx * overlap * 0.5f, b2.getCenterY() + ny * overlap * 0.5f);

                    float rvx = b2.getVx() - b1.getVx();
                    float rvy = b2.getVy() - b1.getVy();
                    float velAlongNormal = rvx * nx + rvy * ny;

                    if (velAlongNormal < 0) {
                        float restitution = 0.9f;
                        float impulseScalar = -(1 + restitution) * velAlongNormal;
                        float totalMass = 2f;
                        float impulseX = (impulseScalar * nx) / totalMass;
                        float impulseY = (impulseScalar * ny) / totalMass;

                        b1.setVx(b1.getVx() - impulseX);
                        b1.setVy(b1.getVy() - impulseY);
                        b2.setVx(b2.getVx() + impulseX);
                        b2.setVy(b2.getVy() + impulseY);
                    }
                }
            }
        }
    }

    // ИСПРАВЛЕНО: Восстановлен оборванный в конце шаблон метода
    private void checkShopZoneCollision() {
        for (int i = greenBalls.size - 1; i >= 0; i--) {
            GreenBall gb = greenBalls.get(i);

            if (shopZone.checkCollision(gb)) {
                // Изменили проверку под рамку 40%: шар должен упасть ниже этой линии, чтобы продаться
                if (gb.getCenterY() < viewport.getWorldHeight() * 0.4f) {
                    money += 10;
                    Gdx.app.log("GAME", "Green ball sold and removed! Coins: " + money);
                    greenBalls.removeIndex(i);
                }
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
