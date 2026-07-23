package io.github.simple_game.core.service;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.model.entity.TowerType;

/**
 * Сервис обработки пользовательского ввода и сложных жестов взаимодействия с игровым миром.
 * Реализует {@link GestureDetector.GestureListener} для поддержки мультитач-жестов,
 * таких как Pinch-to-Zoom (масштабирование), Pan (перетаскивание) и Fling (кинематическая инерция).
 */
public class InteractionService implements GestureDetector.GestureListener {
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;

    private TowerType selectedTowerType = TowerType.ARCHER;
    private final Vector3 touchPoint = new Vector3();

    private static final int CELL_SIZE = 32;

    private float initialZoom = 1.0f;
    private final float minZoom = 0.5f;
    private final float maxZoom = 2.0f;
        // Коэффициент чувствительности перемещения (1.0f — без изменений, 0.5f — в два раза медленнее)
    private static final float PAN_SENSITIVITY = 0.3f;


    // Вектор текущей скорости инерционного движения и коэффициент затухания (трения)
    private final Vector2 velocity = new Vector2();
    private final float friction = 0.90f;

    /**
     * Создает новый сервис взаимодействия и жестов.
     *
     * @param gameLoop актуальная ссылка на игровой цикл
     * @param camera   ортографическаяカメラ игрового мира
     */
    public InteractionService(GameLoop gameLoop, OrthographicCamera camera) {
        this.gameLoop = gameLoop;
        this.camera = camera;
    }

    /**
     * Срабатывает при одиночном коротком тапе по экрану.
     * Используется для постройки и улучшения башен.
     */
    @Override
    public boolean tap(float x, float y, int count, int button) {
        touchPoint.set(x, y, 0);
        camera.unproject(touchPoint);

        float snappedX = ((int) touchPoint.x / CELL_SIZE) * CELL_SIZE + (CELL_SIZE / 2f);
        float snappedY = ((int) touchPoint.y / CELL_SIZE) * CELL_SIZE + (CELL_SIZE / 2f);

        if (canPlaceTower(snappedX, snappedY)) {
            buildTower(snappedX, snappedY);
            return true;
        }
        return false;
    }

    /**
     * Срабатывает при перетаскивании карты одним пальцем по экрану.
     * Сдвигает позицию камеры с учетом масштаба и коэффициента чувствительности,
     * делая перемещение более медленным и контролируемым.
     */
    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        velocity.set(0, 0);

        // Умножаем смещение на PAN_SENSITIVITY, чтобы замедлить движение карты под пальцем
        camera.position.add(-deltaX * camera.zoom * PAN_SENSITIVITY, deltaY * camera.zoom * PAN_SENSITIVITY, 0);

        clampCamera();
        return true;
    }

    /**
     * Срабатывает, когда пользователь резко смахивает карту и отпускает экран.
     * Задает начальный импульс скорости инерции, скорректированный под общую чувствительность.
     */
    @Override
    public boolean fling(float vx, float vy, int button) {
        // Замедляем инерционный бросок соразмерно ручному перетаскиванию
        velocity.set(-vx * camera.zoom * PAN_SENSITIVITY, vy * camera.zoom * PAN_SENSITIVITY);
        return true;
    }


    /**
     * Метод обновления физики инерционного затухания.
     * Должен вызываться каждый кадр из внешнего графического рендерера или игрового экрана.
     * Плавно снижает скорость скольжения за счет коэффициента трения.
     *
     * @param deltaTime время, прошедшее с предыдущего кадра в секундах
     */
    public void updateInertia(float deltaTime) {
        if (velocity.len() < 10f) {
            velocity.set(0, 0);
            return;
        }

        camera.position.add(velocity.x * deltaTime, velocity.y * deltaTime, 0);
        velocity.scl((float) Math.pow(friction, deltaTime * 60f));
        clampCamera();
    }

    /**
     * Математическое ядро жеста Pinch-to-Zoom.
     * Рассчитывает соотношение между начальным расстоянием пальцев и текущим.
     */
    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        float initialDistance = initialPointer1.dst(initialPointer2);
        float currentDistance = pointer1.dst(pointer2);

        if (initialDistance == 0) return false;

        float ratio = initialDistance / currentDistance;
        camera.zoom = Math.max(minZoom, Math.min(maxZoom, initialZoom * ratio));
        clampCamera();
        return true;
    }

    /**
     * Вызывается фреймворком LibGDX в момент, когда жест Pinch-to-Zoom завершается.
     * Фиксирует и сохраняет текущее значение зума камеры.
     */
    @Override
    public void pinchStop() {
        initialZoom = camera.zoom;
    }

    /**
     * Альтернативный метод масштабирования для совместимости с внутренними вызовами фреймворка.
     */
    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance == 0) return false;
        float ratio = initialDistance / distance;
        camera.zoom = Math.max(minZoom, Math.min(maxZoom, initialZoom * ratio));
        clampCamera();
        return true;
    }

    /**
     * Вспомогательный метод, удерживающий объектив камеры строго внутри игровой карты размером 480x800.
     * Предотвращает появление черных областей на экране при сдвиге мира или слишком сильном отдалении.
     */
    private void clampCamera() {
        float worldWidth = 480f;
        float worldHeight = 800f;

        float halfViewportWidth = (camera.viewportWidth * camera.zoom) / 2f;
        float halfViewportHeight = (camera.viewportHeight * camera.zoom) / 2f;

        if (halfViewportWidth * 2f > worldWidth) {
            camera.zoom = worldWidth / camera.viewportWidth;
            halfViewportWidth = worldWidth / 2f;
        }
        if (halfViewportHeight * 2f > worldHeight) {
            camera.zoom = worldHeight / camera.viewportHeight;
            halfViewportHeight = worldHeight / 2f;
        }

        camera.position.x = Math.max(halfViewportWidth, Math.min(worldWidth - halfViewportWidth, camera.position.x));
        camera.position.y = Math.max(halfViewportHeight, Math.min(worldHeight - halfViewportHeight, camera.position.y));
    }

    private boolean canPlaceTower(float x, float y) {
        CurrencyManager economy = gameLoop.getCurrencyManager();
        for (Tower tower : gameLoop.getTowers()) {
            if (tower.getPosition().dst(x, y) < CELL_SIZE) {
                int upgradeCost = tower.getUpgradeCost();
                if (economy.spendGold(upgradeCost)) {
                    tower.tryUpgrade();
                }
                return false;
            }
        }
        return economy.getGold() >= selectedTowerType.getCost();
    }

    private void buildTower(float x, float y) {
        if (selectedTowerType != null) {
            CurrencyManager economy = gameLoop.getCurrencyManager();
            if (economy.spendGold(selectedTowerType.getCost())) {
                Tower newTower = new Tower(x, y, selectedTowerType);
                gameLoop.addTower(newTower);
                System.out.println("Построена башня: " + selectedTowerType + " в координатах (" + x + ", " + y + ")");
            }
        }
    }

    public void setSelectedTowerType(TowerType selectedTowerType) {
        this.selectedTowerType = selectedTowerType;
    }

    @Override public boolean touchDown(float x, float y, int pointer, int button) { return false; }
    @Override public boolean longPress(float x, float y) { return false; }
    @Override public boolean panStop(float x, float y, int pointer, int button) { return false; }
}
