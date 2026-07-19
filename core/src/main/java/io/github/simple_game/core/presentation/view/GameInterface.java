package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.WaveManager;

public class GameInterface {
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont font;

    public GameInterface(GameLoop gameLoop, OrthographicCamera camera) {
        this.gameLoop = gameLoop;
        this.camera = camera;
        this.batch = new SpriteBatch();

        // Используем стандартный встроенный шрифт LibGDX (Arial)
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        // Масштабируем шрифт под наше разрешение экрана
        this.font.getData().setScale(1.5f);
    }

    public void render() {
        // Привязываем отрисовку текста к камере экрана
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        WaveManager waveManager = gameLoop.getWaveManager();

        // Формируем строки для вывода
        String waveText = "Волна: " + waveManager.getCurrentWaveNumber();
        String statusText;

        if (waveManager.isWaveActive()) {
            statusText = "Статус: Идет бой!";
            font.setColor(Color.RED);
        } else {
            statusText = String.format("До следующей волны: %.1f сек", waveManager.getTimeUntilNextWave());
            font.setColor(Color.GOLD);
        }

        // Выводим текст в левом верхнем углу экрана (разрешение экрана 800x480)
        font.draw(batch, waveText, 20, 460);
        font.draw(batch, statusText, 20, 435);

        batch.end();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
