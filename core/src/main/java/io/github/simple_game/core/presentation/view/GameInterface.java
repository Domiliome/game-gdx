package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.simple_game.core.presentation.ui.ShopPanel;
import io.github.simple_game.core.presentation.ui.TopStatusBar;
import io.github.simple_game.core.service.DragAndDropManager; // Новый импорт
import io.github.simple_game.core.service.GameLoop;

/**
 * Главный класс интерфейса, управляющий контейнером Scene2D Stage.
 * Объединяет изолированные виджеты UI в единую адаптивную структуру.
 */
public class GameInterface {
    private final Stage stage;

    /**
     * @param gameLoop    актуальная ссылка на игровой цикл
     * @param uiViewport  выделенный вьюпорт интерфейса (uiViewport из GameScreen)
     * @param renderer    рендерер игрового мира для извлечения текстур башен
     * @param dragManager менеджер перетаскивания для связывания Scene2D с миром
     */
    public GameInterface(GameLoop gameLoop, Viewport uiViewport, GameRenderer renderer, DragAndDropManager dragManager) {
        // Создаем сцену, привязанную к UI-вьюпорту
        this.stage = new Stage(uiViewport);

        // Создаем корневую невидимую таблицу на весь экран устройства
        Table rootTable = new Table();
        rootTable.setFillParent(true);

        // Создаем наши маленькие функциональные модули, передавая им зависимости
        TopStatusBar statusBar = new TopStatusBar(gameLoop);
        ShopPanel shopPanel = new ShopPanel(gameLoop, renderer, dragManager);

        // Верстаем интерфейс: статус-бар идет наверх, панель магазина — строго вниз
        rootTable.add(statusBar).expandX().left().top().pad(20);
        rootTable.row();
        rootTable.add(shopPanel).expand().bottom();

        // Добавляем готовую верстку на сцену
        this.stage.addActor(rootTable);
    }

    /**
     * Обновляет логику анимаций/виджетов и отрисовывает UI на экране.
     */
    public void render() {
        stage.act();
        stage.draw();
    }

    /**
     * Возвращает сцену для регистрации в InputMultiplexer.
     */
    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
    }
}
