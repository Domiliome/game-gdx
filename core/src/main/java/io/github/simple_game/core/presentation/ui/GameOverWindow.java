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

public class GameOverWindow extends Table {
    private final GameLoop gameLoop;
    private final Texture bgTexture;
    private final Label waveLabel;

    public GameOverWindow(GameLoop gameLoop, final Runnable restartAction) {
        this.gameLoop = gameLoop;

        // 1. Полупрозрачный мрачный тёмно-красный фон для экрана поражения
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.25f, 0.02f, 0.02f, 0.92f));
        pixmap.fill();
        this.bgTexture = new Texture(pixmap);
        pixmap.dispose();
        this.setBackground(new TextureRegionDrawable(bgTexture));

        // 2. Стили текста
        Label.LabelStyle titleStyle = new Label.LabelStyle(new BitmapFont(), Color.RED);
        titleStyle.font.getData().setScale(3.5f);
        Label.LabelStyle descStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        descStyle.font.getData().setScale(1.8f);

        Label titleLabel = new Label("GAME OVER", titleStyle);
        waveLabel = new Label("", descStyle);

        // 3. Кнопка RESTART
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = new BitmapFont();
        btnStyle.font.getData().setScale(2.2f);
        btnStyle.fontColor = Color.WHITE;

        TextButton restartBtn = new TextButton(" TRY AGAIN ", btnStyle);
        restartBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (restartAction != null) restartAction.run();
            }
        });

        // 4. Верстка
        this.center();
        this.add(titleLabel).padBottom(20).row();
        this.add(waveLabel).padBottom(40).row();
        this.add(restartBtn).size(280, 70);
        this.setVisible(false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Окно включается, если у игрока закончились жизни (lives <= 0)
        boolean isDead = gameLoop.getCurrencyManager().getLives() <= 0;
        this.setVisible(isDead);

        if (isDead) {
            int wave = gameLoop.getWaveManager().getCurrentWaveNumber();
            waveLabel.setText("You reached Wave: " + wave + "\nYour persistent items are saved.");
            waveLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        }
    }

    public void dispose() {
        if (bgTexture != null) bgTexture.dispose();
    }
}
