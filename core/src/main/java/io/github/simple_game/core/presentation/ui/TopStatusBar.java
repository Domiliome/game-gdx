package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.simple_game.core.Main;
import io.github.simple_game.core.presentation.screen.GameScreen;
import io.github.simple_game.core.service.CurrencyManager;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.WaveManager;

public class TopStatusBar extends Table {
    private static final float ICON_SIZE = 56f;
    private static final float FONT_SCALE = 1.4f;

    private final GameLoop gameLoop;
    private final Label waveLabel, statusLabel, economyLabel;
    private final Image startButton, bagImage;
    private final Texture backpackTex, startTex, pauseTex;
    private final TextureRegionDrawable startDrawable, pauseDrawable;

    public TopStatusBar(GameLoop gameLoop, final Main game, final GameScreen gameScreen) {
        this.gameLoop = gameLoop;

        backpackTex = new Texture(Gdx.files.internal("ui/icons/backpack.png"));
        startTex = new Texture(Gdx.files.internal("ui/icons/start.png"));
        pauseTex = new Texture(Gdx.files.internal("ui/icons/pause.png"));

        backpackTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        startTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pauseTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        this.startDrawable = new TextureRegionDrawable(new TextureRegion(startTex));
        this.pauseDrawable = new TextureRegionDrawable(new TextureRegion(pauseTex));

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        labelStyle.font.getData().setScale(FONT_SCALE);

        waveLabel = new Label("", labelStyle);
        statusLabel = new Label("", labelStyle);
        economyLabel = new Label("", labelStyle);

        startButton = new Image(startDrawable);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                WaveManager waveManager = TopStatusBar.this.gameLoop.getWaveManager();
                if (!waveManager.isWaveActive()) {
                    waveManager.startNextWave();
                } else {
                    boolean currentPauseState = TopStatusBar.this.gameLoop.isPaused();
                    TopStatusBar.this.gameLoop.setPaused(!currentPauseState);
                }
            }
        });

        bagImage = new Image(backpackTex);
        bagImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new io.github.simple_game.core.presentation.screen.InventoryScreen(
                        game, gameScreen, TopStatusBar.this.gameLoop));
            }
        });

        Table textTable = new Table();
        textTable.left().top();
        textTable.add(waveLabel).left().padBottom(4).row();
        textTable.add(statusLabel).left().padBottom(4).row();
        textTable.add(economyLabel).left();

        Table buttonTable = new Table();
        buttonTable.right().top();
        buttonTable.add(startButton).size(ICON_SIZE, ICON_SIZE).padRight(4);
        buttonTable.add(bagImage).size(ICON_SIZE, ICON_SIZE);

        this.left().top().pad(10);
        this.add(textTable).expandX().left().top();
        this.add(buttonTable).right().top();
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
            if (gameLoop.isPaused()) {
                statusLabel.setText("Status: Paused");
                statusLabel.setColor(Color.GOLD);
                startButton.setDrawable(startDrawable);
            } else {
                statusLabel.setText("Status: Battle!");
                statusLabel.setColor(Color.RED);
                startButton.setDrawable(pauseDrawable);
            }
        } else {
            statusLabel.setText("Next wave: Ready");
            statusLabel.setColor(Color.GOLD);
            startButton.setDrawable(startDrawable);
        }
    }

    public void dispose() {
        if (backpackTex != null) backpackTex.dispose();
        if (startTex != null) startTex.dispose();
        if (pauseTex != null) pauseTex.dispose();
    }
}
