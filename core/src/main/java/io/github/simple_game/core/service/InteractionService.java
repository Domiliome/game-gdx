package io.github.simple_game.core.service;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import io.github.simple_game.core.model.entity.Tower;
import io.github.simple_game.core.model.entity.TowerType;

/**
 * Сервис обработки пользовательского ввода и взаимодействия с игровым миром.
 * Наследуется от {@link InputAdapter} для перехвата событий нажатий. Отвечает за
 * трансформацию экранных координат в мировые координаты, выравнивание объектов
 * по сетке (Grid Snapping), а также за постройку и улучшение башен.
 */
public class InteractionService extends InputAdapter {
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;

    private TowerType selectedTowerType = TowerType.ARCHER;
    private final Vector3 touchPoint = new Vector3();

    private static final int CELL_SIZE = 32;

    /**
     * Создает новый сервис взаимодействия.
     *
     * @param gameLoop актуальная ссылка на игровой цикл для добавления и проверки башен
     * @param camera   ортографическая камера игрового мира для депроекции координат экрана
     */
    public InteractionService(GameLoop gameLoop, OrthographicCamera camera) {
        this.gameLoop = gameLoop;
        this.camera = camera;
    }

    /**
     * Обрабатывает событие нажатия на экран смартфона или клика мышкой на ПК.
     * Преобразует координаты клика, выполняет привязку к центру ближайшей игровой
     * ячейки и инициирует логику строительства либо улучшения башни.
     *
     * @param screenX   координата X точки нажатия на физическом экране
     * @param screenY   координата Y точки нажатия на физическом экране (начиная сверху)
     * @param pointer   индекс пальца для мультитача
     * @param button    код нажатой кнопки мыши
     * @return true, если событие успешно обработано сервисом; иначе false
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPoint.set(screenX, screenY, 0);
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
     * Проверяет доступность указанных координат для возведения новой башни.
     * Сканирует список существующих башен на предмет пересечения границ ячейки.
     * Если в целевых координатах уже установлена башня, метод автоматически
     * запрашивает попытку её апгрейда.
     *
     * @param x выровненная по сетке координата X потенциальной постройки
     * @param y выровненная по сетке координата Y потенциальной постройки
     * @return true, если клетка абсолютно свободна для строительства; false, если ячейка занята
     */
    private boolean canPlaceTower(float x, float y) {
        for (Tower tower : gameLoop.getTowers()) {
            if (tower.getPosition().dst(x, y) < CELL_SIZE) {
                tower.tryUpgrade();
                return false;
            }
        }
        return true;
    }

    /**
     * Реализует непосредственное возведение оборонительного сооружения.
     * Создает экземпляр объекта класса {@link Tower} с выбранным типом и
     * регистрирует его в центральном игровом цикле.
     *
     * @param x выровненная координата X для центра башни
     * @param y выровненная координата Y для центра башни
     */
    private void buildTower(float x, float y) {
        if (selectedTowerType != null) {
            Tower newTower = new Tower(x, y, selectedTowerType);
            gameLoop.addTower(newTower);
            System.out.println("Построена башня: " + selectedTowerType + " в координатах (" + x + ", " + y + ")");
        }
    }

    /**
     * Изменяет текущий тип башни, который будет возводиться при последующих кликах на карту.
     * Обычно вызывается из обработчиков интерфейса UI (кнопок магазина).
     *
     * @param selectedTowerType новый целевой тип башни для переключения конструкции
     */
    public void setSelectedTowerType(TowerType selectedTowerType) {
        this.selectedTowerType = selectedTowerType;
    }
}
