package io.github.simple_game.core.service;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.model.entity.TowerType;

public class InteractionService extends InputAdapter {
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;

    // Переменная для хранения выбранного типа башни для постройки
    private TowerType selectedTowerType = TowerType.ARCHER;

    // Временный вектор, чтобы избежать создания новых объектов в памяти при каждом клике
    private final Vector3 touchPoint = new Vector3();

    // Размер клетки сетки (например, 64x64 пикселя)
    private static final int CELL_SIZE = 64;

    public InteractionService(GameLoop gameLoop, OrthographicCamera camera) {
        this.gameLoop = gameLoop;
        this.camera = camera;
    }

    /**
     * Переопределяем метод touchDown, который срабатывает при тапе по экрану или клике мышкой
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // 1. Переводим координаты экрана в координаты игрового мира с учетом зума и позиции камеры
        touchPoint.set(screenX, screenY, 0);
        camera.unproject(touchPoint);

        // 2. Привязываем координаты к сетке (Grid Snapping), чтобы башни ровно вставали в клетки
        float snappedX = ((int) touchPoint.x / CELL_SIZE) * CELL_SIZE + (CELL_SIZE / 2f);
        float snappedY = ((int) touchPoint.y / CELL_SIZE) * CELL_SIZE + (CELL_SIZE / 2f);

        // 3. Проверяем, можно ли построить башню (например, нет ли там уже другой башни)
        if (canPlaceTower(snappedX, snappedY)) {
            buildTower(snappedX, snappedY);
            return true; // Событие обработано
        }

        return false;
    }

    /**
     * Проверка возможности установки башни
     */
    private boolean canPlaceTower(float x, float y) {
        // Проверяем, не накладывается ли новая башня на уже существующие
        for (Tower tower : gameLoop.getTowers()) {
            // Если расстояние между точками меньше размера клетки, значит клетка занята
            if (tower.getPosition().dst(x, y) < CELL_SIZE) {
                // Если кликнули на существующую башню — можно предложить её улучшить
                tower.tryUpgrade();
                return false;
            }
        }

        // В будущем здесь также будет проверка:
        // 1. Хватает ли у игрока золота: CurrencyManager.getGold() >= selectedTowerType.getCost()
        // 2. Не ставим ли мы башню прямо на дорогу (проверка по сетке карты)

        return true;
    }

    /**
     * Логика постройки башни
     */
    private void buildTower(float x, float y) {
        if (selectedTowerType != null) {
            Tower newTower = new Tower(x, y, selectedTowerType);
            gameLoop.addTower(newTower);
            System.out.println("Построена башня: " + selectedTowerType + " в координатах (" + x + ", " + y + ")");

            // Здесь нужно отнять золото у игрока:
            // CurrencyManager.spendGold(selectedTowerType.getCost());
        }
    }

    /**
     * Метод для смены типа башни (например, при нажатии на кнопку в UI)
     */
    public void setSelectedTowerType(TowerType selectedTowerType) {
        this.selectedTowerType = selectedTowerType;
    }
}
