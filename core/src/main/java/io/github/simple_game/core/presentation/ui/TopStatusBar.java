package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import io.github.simple_game.core.service.CurrencyManager;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.WaveManager;

public class TopStatusBar extends Table {
    private final GameLoop gameLoop;
    private final Label waveLabel;
    private final Label statusLabel;
    private final Label economyLabel;

    public TopStatusBar(GameLoop gameLoop) {
        this.gameLoop = gameLoop;

        // Создаем дефолтный стиль для текста
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        // Увеличиваем базовый размер шрифта для мобильных устройств
        labelStyle.font.getData().setScale(1.5f);

        // Инициализируем метки
        waveLabel = new Label("", labelStyle);
        statusLabel = new Label("", labelStyle);
        economyLabel = new Label("", labelStyle);

        // Выстраиваем их вертикально в левом углу таблицы
        this.left().top();
        this.add(waveLabel).left().padBottom(5);
        this.row();
        this.add(statusLabel).left().padBottom(5);
        this.row();
        this.add(economyLabel).left();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        WaveManager waveManager = gameLoop.getWaveManager();
        CurrencyManager economy = gameLoop.getCurrencyManager();

        // 1. Обновляем текст волны
        waveLabel.setText("Wave: " + waveManager.getCurrentWaveNumber());

        // 2. Обновляем статус
        if (waveManager.isWaveActive()) {
            statusLabel.setText("Status: Battle in progress!");
            statusLabel.setColor(Color.RED);
        } else {
            statusLabel.setText(String.format("Next wave: %.1f sec", waveManager.getTimeUntilNextWave()));
            statusLabel.setColor(Color.GOLD);
        }

        // 3. Обновляем экономику
        economyLabel.setText("Gold: " + economy.getGold() + "  |  Lives: " + economy.getLives());
        economyLabel.setColor(Color.GREEN);
    }
}
