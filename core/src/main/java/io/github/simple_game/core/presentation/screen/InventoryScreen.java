package io.github.simple_game.core.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.simple_game.core.Main;
import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.service.GameLoop;

public class InventoryScreen extends ScreenAdapter {
    private final Main game;
    private final GameScreen gameScreen;
    private final GameLoop gameLoop;
    private final Stage stage;
    private final Texture btnBg, slotBg;
    private Label descLabel; // Ссылка на лейбл описания внизу экрана

    public InventoryScreen(Main game, GameScreen gameScreen, GameLoop gameLoop) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.gameLoop = gameLoop;
        this.stage = new Stage(new ScreenViewport());

        // Программная генерация текстур для кнопок и квадратных ячеек
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.3f, 0.3f, 0.3f, 1f)); pixmap.fill();
        this.btnBg = new Texture(pixmap);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.2f, 1f)); pixmap.fill();
        this.slotBg = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        Label.LabelStyle textStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        textStyle.font.getData().setScale(1.8f);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = new BitmapFont(); btnStyle.font.getData().setScale(2.0f);
        btnStyle.fontColor = Color.WHITE; btnStyle.up = new TextureRegionDrawable(btnBg);

        TextButton.TextButtonStyle slotStyle = new TextButton.TextButtonStyle();
        slotStyle.font = new BitmapFont(); slotStyle.font.getData().setScale(1.3f);
        slotStyle.fontColor = Color.GOLD; slotStyle.up = new TextureRegionDrawable(slotBg);

        // Главная таблица экрана
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().pad(30);

        Label title = new Label("--- INVENTORY ---", textStyle);
        title.getStyle().font.getData().setScale(2.5f);
        mainTable.add(title).padBottom(20).row();

        // 1. Создаем внутреннюю СЕТКУ (Grid) предметов для прокрутки
        Table gridTable = new Table();
        gridTable.top().left();
        var backpack = this.gameLoop.getInventoryManager().getBackpack();
        int columns = 4; // Количество ячеек в одной горизонтальной строке

        for (int i = 0; i < backpack.size; i++) {
            final Item item = backpack.get(i);
            // Берем первые 2 буквы имени предмета как временную "иконку" значка
            String iconText = item.getName().substring(0, Math.min(2, item.getName().length())).toUpperCase();
            TextButton slotBtn = new TextButton("[" + iconText + "]", slotStyle);

            slotBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // При клике на квадратный значок обновляем текст описания внизу экрана!
                    descLabel.setText(item.getName() + "\n" + item.getDescription());
                }
            });

            gridTable.add(slotBtn).size(80, 80).pad(10); // Квадратные значки 80x80
            if ((i + 1) % columns == 0) gridTable.row(); // Перенос строки сетки
        }

        // 2. Оборачиваем сетку в ScrollPane для бесконечной прокрутки вверх/вниз
        ScrollPane scrollPane = new ScrollPane(gridTable);
        scrollPane.setScrollingDisabled(true, false); // Запрещаем горизонтальный скролл, разрешаем вертикальный
        mainTable.add(scrollPane).expand().fill().padBottom(20).row();

        // 3. НИЖНЯЯ ПАНЕЛЬ: Название и описание выбранного предмета
        descLabel = new Label("Select an item to see description", textStyle);
        descLabel.setColor(Color.LIGHT_GRAY);
        descLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        mainTable.add(descLabel).width(420).height(100).padBottom(20).row();

        // 4. Кнопка возврата в игру
        TextButton backBtn = new TextButton(" RETURN TO GAME ", btnStyle);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                InventoryScreen.this.game.setScreen(InventoryScreen.this.gameScreen);
            }
        });
        mainTable.add(backBtn).size(320, 65);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.14f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }

    @Override
    public void dispose() {
        stage.dispose();
        btnBg.dispose();
        slotBg.dispose();
    }
}
