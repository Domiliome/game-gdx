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


    private RedBall redBall;
    private GreenBall greenBall;

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

            // ДОБАВЛЕНО: Проверяем и обрабатываем столкновение шаров между собой
            checkBallCollision();

            physicsAccumulator -= TIME_STEP;
        }

        // --- ОТРИСОВКА ГРАФИКИ ---
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);

        // Отрисовываем шары
        redBall.draw(shapeRenderer);
        greenBall.draw(shapeRenderer);

        // Рисуем рамки и линии игрового поля
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);

        // 1. Рисуем внешнюю белую рамку поля
        shapeRenderer.rect(PADDING, PADDING, viewport.getWorldWidth() - (PADDING * 2f), viewport.getWorldHeight() - (PADDING * 2f));

        // 2. ДОБАВЛЕНО: Рисуем полупрозрачную разделительную линию на уровне 1/3 экрана
        shapeRenderer.setColor(1f, 1f, 1f, 0.3f); // Слегка тусклый белый цвет (alpha = 0.3)
        float lineY = viewport.getWorldHeight() / 3f;
        shapeRenderer.line(PADDING, lineY, viewport.getWorldWidth() - PADDING, lineY);

        shapeRenderer.end();
    }

    // ДОБАВЛЕНО: Математический метод обработки упругого соударения двух окружностей
    private void checkBallCollision() {
        // Расстояние между центрами по осям X и Y
        float dx = greenBall.getCenterX() - redBall.getCenterX();
        float dy = greenBall.getCenterY() - redBall.getCenterY();

        // Квадрат расстояния и само расстояние (Теорема Пифагора)
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Минимальное расстояние, при котором шары касаются друг друга
        float minDistance = redBall.getCurrentRadius() + greenBall.getCurrentRadius();

        // Если шары зашли друг за друга — фиксируем удар
        if (distance < minDistance) {
            if (distance == 0) return; // Защита от деления на ноль, если центры совпали

            // Вектор нормали столкновения (направление удара)
            float nx = dx / distance;
            float ny = dy / distance;

            // Величина наложения шаров друг на друга
            float overlap = minDistance - distance;

            // Расталкиваем шары наружу, чтобы они мгновенно вышли из зацепления и не слипались
            redBall.setPosition(redBall.getCenterX() - nx * overlap * 0.5f, redBall.getCenterY() - ny * overlap * 0.5f);
            greenBall.setPosition(greenBall.getCenterX() + nx * overlap * 0.5f, greenBall.getCenterY() + ny * overlap * 0.5f);

            // Вычисляем относительную скорость шаров
            float rvx = greenBall.getVx() - redBall.getVx();
            float rvy = greenBall.getVy() - redBall.getVy();


            float velAlongNormal = rvx * nx + rvy * ny;

            // Считаем отскок только если они летят НАВСТРЕЧУ друг другу, а не РАЗЛЕТАЮТСЯ
            if (velAlongNormal < 0) {
                // Прыгучесть при ударе шаров (0.8f — сохраняет 80% энергии)
                float restitution = 0.8f;

                // Импульс силы
                float impulseScalar = -(1 + restitution) * velAlongNormal;

                // Распределяем силу с учетом размеров (массы) шаров
                float redMass = redBall.getCurrentRadius();
                float greenMass = greenBall.getCurrentRadius();
                float totalMass = redMass + greenMass;

                float impulseX = (impulseScalar * nx) / totalMass;
                float impulseY = (impulseScalar * ny) / totalMass;

                // Передаем новые скорости в оба класса через сеттеры
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
