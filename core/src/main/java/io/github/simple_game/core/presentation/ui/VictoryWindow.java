package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.simple_game.core.service.GameLoop;

public class VictoryWindow extends Table {
    private final GameLoop gameLoop;
    private final Texture bgTexture;

    public VictoryWindow(GameLoop gameLoop, final Runnable restartAction) {
        this.gameLoop = gameLoop;

        // 1. Создаем полупрозрачный красивый изумрудно-зеленый фон окна победы
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.02f, 0.25f, 0.08f, 0.92f));
        pixmap.fill();
        this.bgTexture = new Texture(pixmap);
        pixmap.dispose();
        this.setBackground(new TextureRegionDrawable(bgTexture));

        // 2. Стили текста (Крупный масштаб)
        Label.LabelStyle titleStyle = new Label.LabelStyle(new BitmapFont(), Color.GOLD);
        titleStyle.font.getData().setScale(3.5f);
        Label.LabelStyle descStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        descStyle.font.getData().setScale(1.8f);

        Label titleLabel = new Label("VICTORY!", titleStyle);
        Label descLabel = new Label("You defended all 20 waves!\nYour loot is safe in backpack.", descStyle);
        descLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

        // 3. Стиль кнопки NEXT CAMPAIGN / RESTART
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = new BitmapFont();
        btnStyle.font.getData().setScale(2.2f);
        btnStyle.fontColor = Color.GOLD;

        TextButton restartBtn = new TextButton(" PLAY AGAIN ", btnStyle);
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (restartAction != null) restartAction.run();
            }
        });

        // 4. Плотная верстка элементов
        this.center();
        this.add(titleLabel).padBottom(25).row();
        this.add(descLabel).padBottom(40).row();
        this.add(restartBtn).size(280, 70);
        this.setVisible(false); // Изначально окно скрыто
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Окно становится видимым ТОЛЬКО когда в игровом цикле сработал флаг победы
        this.setVisible(gameLoop.isVictory());
    }

    public void dispose() {
        if (bgTexture != null) bgTexture.dispose();
    }
}
