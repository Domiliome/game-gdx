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
import io.github.simple_game.core.service.CurrencyManager;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.WaveManager;

public class TopStatusBar extends Table {
    private final GameLoop gameLoop;
    private final Label waveLabel, statusLabel, economyLabel;
    private final TextButton startButton;
    private final Texture btnBg;

    public TopStatusBar(GameLoop gameLoop) {
        this.gameLoop = gameLoop;

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        labelStyle.font.getData().setScale(1.5f);

        waveLabel = new Label("", labelStyle);
        statusLabel = new Label("", labelStyle);
        economyLabel = new Label("", labelStyle);

        // 1. Генерация программной текстуры для фона кнопки
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.6f, 0.2f, 1f)); // Зеленая кнопка
        pixmap.fill();
        this.btnBg = new Texture(pixmap);
        pixmap.dispose();

        // 2. Создание стиля кнопки без использования внешних skins JSON
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = new BitmapFont();
        btnStyle.font.getData().setScale(1.4f);
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = new TextureRegionDrawable(btnBg); // Фон кнопки в обычном состоянии

        startButton = new TextButton(" START WAVE ", btnStyle);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Вызываем запуск следующей волны в нашем менеджере
                gameLoop.getWaveManager().startNextWave();
            }
        });

        // 3. Верстка: Текст собирается в левую мини-таблицу, кнопка идет справа от него
        Table textTable = new Table();
        textTable.left();
        textTable.add(waveLabel).left().padBottom(5).row();
        textTable.add(statusLabel).left().padBottom(5).row();
        textTable.add(economyLabel).left();

        this.left().top();
        this.add(textTable).expandX().left();
        this.add(startButton).right().size(160, 60).padRight(20); // Кнопка прижата к правому краю
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        WaveManager waveManager = gameLoop.getWaveManager();
        CurrencyManager economy = gameLoop.getCurrencyManager();

        waveLabel.setText("Wave: " + waveManager.getCurrentWaveNumber());
        economyLabel.setText("Gold: " + economy.getGold() + "  |  Lives: " + economy.getLives());
        economyLabel.setColor(Color.GREEN);

        // Управляем доступностью кнопки и текстом в реальном времени
        if (waveManager.isWaveActive()) {
            statusLabel.setText("Status: Battle!");
            statusLabel.setColor(Color.RED);
            startButton.setVisible(false); // Прячем кнопку во время активного боя
        } else {
            statusLabel.setText("Next wave: Ready");
            statusLabel.setColor(Color.GOLD);
            startButton.setVisible(true);  // Показываем кнопку в фазе отдыха
        }
    }
}
