package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
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

import io.github.simple_game.core.Main;
import io.github.simple_game.core.presentation.screen.GameScreen;
import io.github.simple_game.core.service.CurrencyManager;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.WaveManager;

public class TopStatusBar extends Table {
    private final GameLoop gameLoop;
    private final Label waveLabel, statusLabel, economyLabel;
    private final TextButton startButton, bagButton;
    private final Texture btnBg;

    public TopStatusBar(GameLoop gameLoop, final Main game, final GameScreen gameScreen) {
        this.gameLoop = gameLoop;

        float scale = (Gdx.app.getType() == Application.ApplicationType.Android) ? 2.0f : 1.0f;

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        labelStyle.font.getData().setScale(1.5f * scale);

        waveLabel = new Label("", labelStyle);
        statusLabel = new Label("", labelStyle);
        economyLabel = new Label("", labelStyle);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.6f, 0.2f, 1f));
        pixmap.fill();
        this.btnBg = new Texture(pixmap);
        pixmap.dispose();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = new BitmapFont();
        btnStyle.font.getData().setScale(1.4f * scale);
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = new TextureRegionDrawable(btnBg);

        startButton = new TextButton(" START ", btnStyle);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TopStatusBar.this.gameLoop.getWaveManager().startNextWave();
            }
        });

        bagButton = new TextButton(" BAG ", btnStyle);
        bagButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new io.github.simple_game.core.presentation.screen.InventoryScreen(game, gameScreen, TopStatusBar.this.gameLoop));

            }
        });

        Table textTable = new Table();
        textTable.left();
        textTable.add(waveLabel).left().padBottom(5 * scale).row();
        textTable.add(statusLabel).left().padBottom(5 * scale).row();
        textTable.add(economyLabel).left();

        this.left().top();
        this.add(textTable).expandX().left();

        this.add(bagButton).right().size(120 * scale, 60 * scale).padRight(10 * scale);
        this.add(startButton).right().size(160 * scale, 60 * scale).padRight(20 * scale);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        WaveManager waveManager = gameLoop.getWaveManager();
        CurrencyManager economy = gameLoop.getCurrencyManager();

        waveLabel.setText("Wave: " + waveManager.getCurrentWaveNumber());
        economyLabel.setText("Gold: " + economy.getGold() + "  |  Lives: " + economy.getLives());
        economyLabel.setColor(Color.GREEN);

        if (waveManager.isWaveActive()) {
            statusLabel.setText("Status: Battle!");
            statusLabel.setColor(Color.RED);
            startButton.setVisible(false);
        } else {
            statusLabel.setText("Next wave: Ready");
            statusLabel.setColor(Color.GOLD);
            startButton.setVisible(true);
        }
    }

    public void dispose() {
        if (btnBg != null) btnBg.dispose();
    }
}
