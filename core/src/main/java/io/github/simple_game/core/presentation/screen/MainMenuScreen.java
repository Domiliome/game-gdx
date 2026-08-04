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

public class MainMenuScreen extends ScreenAdapter {
    private final Main game;
    private final Stage stage;
    private final Texture btnBg;

    public MainMenuScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.22f, 1f)); pixmap.fill();
        this.btnBg = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear();

        Label.LabelStyle titleStyle = new Label.LabelStyle(new BitmapFont(), Color.GOLD);
        titleStyle.font.getData().setScale(3.5f);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = new BitmapFont(); btnStyle.font.getData().setScale(2.2f);
        btnStyle.fontColor = Color.WHITE; btnStyle.up = new TextureRegionDrawable(btnBg);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(new Label("TOWER DEFENSE RPG", titleStyle)).padBottom(60).row();

        TextButton startBtn = new TextButton(" START GAME ", btnStyle);
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });
        table.add(startBtn).size(340, 75).padBottom(20).row();

        TextButton bagBtn = new TextButton(" BAG ", btnStyle);
        bagBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // ИСПРАВЛЕНО: Передаем СЕБЯ (MainMenuScreen.this) вторым аргументом в InventoryScreen.
                // Теперь полиморфная кнопка RETURN в инвентаре безошибочно вернет игрока назад в меню!
                game.setScreen(new InventoryScreen(game, MainMenuScreen.this, game.getGlobalInventoryGameLoop()));
            }
        });
        table.add(bagBtn).size(340, 75).padBottom(20).row();

        TextButton exitBtn = new TextButton(" EXIT ", btnStyle);
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(exitBtn).size(340, 75);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void dispose() { stage.dispose(); btnBg.dispose(); }
}
