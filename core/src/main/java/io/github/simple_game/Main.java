package io.github.simple_game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

    // Две независимые ссылки на разные классы шаров
    private RedBall redBall;
    private GreenBall greenBall;

    // ДОБАВЛЕНО: Объект зоны продажи и переменная баланса игрока
    private ShopZone shopZone;
    private int money = 0;

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

        // Инициализируем каждый шар своим классом
        redBall = new RedBall(WORLD_WIDTH / 2f - 80f, WORLD_HEIGHT / 2f, viewport, PADDING);
        greenBall = new GreenBall(WORLD_WIDTH / 2f + 80f, WORLD_HEIGHT / 2f, viewport, PADDING);

        // ДОБАВЛЕНО: Создаем зону продажи в углу с размером 80 на 80 пикселей
        shopZone = new ShopZone(PADDING, 80f);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        if (deltaTime > 0.25f) deltaTime = 0.25f;

        // --- ФИКСИРОВАННЫЙ ШАГ ФИЗИКИ ---
        physicsAccumulator += deltaTime;
        while (physicsAccumulator >= TIME_STEP) {
            // Запускаем расчет физики для каждого шара отдельно
            redBall.update(TIME_STEP);
            greenBall.update(TIME_STEP);

            // Проверяем и обрабатываем столкновение шаров между собой
            checkBallCollision();

            // ДОБАВЛЕНО: Проверяем, попал ли салатовый шар в зону продажи
            if (shopZone.checkCollision(greenBall)) {
                money += 10; // Зачисляем валюту
                Gdx.app.log("GAME", "Салатовый шар продан! Текущий баланс: " + money);

                // Телепортируем салатовый шар обратно в правый верхний угол игрового поля
                float startX = viewport.getWorldWidth() / 2f + 80f;
                float startY = viewport.getWorldHeight() - PADDING - 100f;
                greenBall.setPosition(startX, startY);

                // Сбрасываем его скорость для плавного нового падения
                greenBall.setVx(0f);
                greenBall.setVy(0f);
            }

            physicsAccumulator -= TIME_STEP;
        }

        // --- ОТРИСОВКА ГРАФИКИ ---
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);

        // ДОБАВЛЕНО: Рисуем заполненную зеленым цветом зону продажи
        shopZone.draw(shapeRenderer);

        // Отрисовываем шары поверх зоны
        redBall.draw(shapeRenderer);
        greenBall.draw(shapeRenderer);

        // Рисуем рамки и линии игрового поля
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);

        // 1. Рисуем внешнюю белую рамку поля
        shapeRenderer.rect(PADDING, PADDING, viewport.getWorldWidth() - (PADDING * 2f), viewport.getWorldHeight() - (PADDING * 2f));

        // 2. Рисуем полупрозрачную разделительную линию на уровне 1/3 экрана
        shapeRenderer.setColor(1f, 1f, 1f, 0.3f);
        float lineY = viewport.getWorldHeight() / 3f;
        shapeRenderer.line(PADDING, lineY, viewport.getWorldWidth() - PADDING, lineY);

        shapeRenderer.end();
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
                float totalMass = redMass + greenMass;

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
    }
}
