package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.entity.Projectile;
import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.service.GameLoop;

/**
 * Класс подсистемы отрисовки графики игрового мира.
 * Отвечает за визуализацию всех сущностей на карте (башен, врагов, снарядов),
 * дороги уровня, дебаг-информации и полосок здоровья.
 */
public class GameRenderer {
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    /**
     * Создает новый рендерер игрового мира.
     * Инициализирует графические инструменты для вывода текстур и примитивных фигур.
     *
     * @param gameLoop актуальная ссылка на игровой цикл для получения списков объектов
     * @param camera   ортографическая камера экрана для синхронизации проекции
     */
    public GameRenderer(GameLoop gameLoop, OrthographicCamera camera) {
        this.gameLoop = gameLoop;
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
    }

    /**
     * Главный метод рендеринга кадра, вызываемый из игрового экрана.
     * Синхронизирует матрицы инструментов отрисовки с камерой и последовательно
     * выполняет отрисовку геометрических фигур и графических спрайтов.
     */
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        renderShapes();
        renderSprites();
    }

    /**
     * Визуализирует геометрические элементы игрового мира через {@link ShapeRenderer}.
     * Сюда входит отрисовка линий маршрута, кругов радиуса атаки башен и полосок здоровья врагов.
     */
    private void renderShapes() {
        RoadPath path = (RoadPath) gameLoop.getRoadPath();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);
        for (int i = 0; i < path.getPointCount() - 1; i++) {
            shapeRenderer.line(path.getPoint(i), path.getPoint(i + 1));
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.3f));
        for (Tower tower : gameLoop.getTowers()) {
            shapeRenderer.circle(tower.getPosition().x, tower.getPosition().y, tower.getAttackRange());
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : gameLoop.getEnemies()) {
            float enemyX = enemy.getPosition().x;
            float enemyY = enemy.getPosition().y;
            float barWidth = 30f;
            float barHeight = 4f;
            float barYOffset = 25f;

            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(enemyX - barWidth / 2, enemyY + barYOffset, barWidth, barHeight);

            float healthPercentage = Math.max(0f, enemy.getHealth() / 100f);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(enemyX - barWidth / 2, enemyY + barYOffset, barWidth * healthPercentage, barHeight);
        }
        shapeRenderer.end();
    }

    /**
     * Визуализирует растровые спрайты и текстуры игровых объектов через {@link SpriteBatch}.
     * На этапе прототипирования временно использует цветные круги в качестве заглушек сущностей.
     */
    private void renderSprites() {
        batch.begin();
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.PURPLE);
        for (Enemy enemy : gameLoop.getEnemies()) {
            shapeRenderer.circle(enemy.getPosition().x, enemy.getPosition().y, 12);
        }

        shapeRenderer.setColor(Color.YELLOW);
        for (Projectile projectile : gameLoop.getProjectiles()) {
            shapeRenderer.circle(projectile.getPosition().x, projectile.getPosition().y, 4);
        }
        shapeRenderer.end();
    }

    /**
     * Освобождает системные ресурсы, занятые подсистемой рендеринга.
     * Принудительно очищает контексты {@link SpriteBatch} и {@link ShapeRenderer} из видеопамяти.
     */
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }
}
