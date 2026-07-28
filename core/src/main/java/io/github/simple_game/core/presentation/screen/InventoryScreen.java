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
    private final Texture btnBg;

    public InventoryScreen(Main game, GameScreen gameScreen, GameLoop gameLoop) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.gameLoop = gameLoop;
        this.stage = new Stage(new ScreenViewport());

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.3f, 0.3f, 0.3f, 1f));
        pixmap.fill();
        this.btnBg = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear(); // Очищаем старые элементы сцены перед новой версткой

        Label.LabelStyle titleStyle = new Label.LabelStyle(new BitmapFont(), Color.GOLD);
        titleStyle.font.getData().setScale(3.0f);
        Label.LabelStyle itemStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        itemStyle.font.getData().setScale(1.8f);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = new BitmapFont();
        btnStyle.font.getData().setScale(2.0f);
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = new TextureRegionDrawable(btnBg);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().pad(40);

        rootTable.add(new Label("--- MY BACKPACK ---", titleStyle)).padBottom(30).row();

        // Исправлено: Читаем данные СТРОГО через поля класса, убирая все Warnings IDE!
        var backpack = this.gameLoop.getInventoryManager().getBackpack();
        if (backpack.size == 0) {
            Label empty = new Label("Your backpack is empty...", itemStyle);
            empty.setColor(Color.GRAY);
            rootTable.add(empty).padBottom(40).row();
        } else {
            for (Item item : backpack) {
                Table itemRow = new Table();
                Label name = new Label("• " + item.getName(), itemStyle);
                name.setColor(Color.CYAN);
                Label desc = new Label(" (" + item.getDescription() + ")", itemStyle);
                desc.setColor(Color.LIGHT_GRAY);

                itemRow.add(name).left();
                itemRow.add(desc).left().padLeft(15);
                rootTable.add(itemRow).left().padBottom(15).row();
            }
        }

        TextButton backBtn = new TextButton(" RETURN TO GAME ", btnStyle);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Исправлено: Апеллируем к полю класса для безопасного возвращения на игровой экран
                InventoryScreen.this.game.setScreen(InventoryScreen.this.gameScreen);
            }
        });
        rootTable.add(backBtn).size(300, 70).padTop(30);

        stage.addActor(rootTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        btnBg.dispose();
    }
}
