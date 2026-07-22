package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.WaveManager;

/**
 * Класс, отвечающий за отрисовку пользовательского интерфейса (UI) поверх игрового экрана.
 * Использует {@link BitmapFont} для рендеринга текстовой информации, получая актуальные данные
 * о состоянии волн и таймингов из {@link WaveManager}.
 */
public class GameInterface {
    private final GameLoop gameLoop;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final BitmapFont font;

    /**
     * Создает новый графический интерфейс игры.
     * Настраивает стандартный шрифт, цвет вывода по умолчанию и масштабирует его
     * под виртуальное разрешение экрана.
     *
     * @param gameLoop актуальная ссылка на игровой цикл для извлечения состояния игрового мира
     * @param camera   ортографическая камера экрана для синхронизации матриц проекции
     */
    public GameInterface(GameLoop gameLoop, OrthographicCamera camera) {
        this.gameLoop = gameLoop;
        this.camera = camera;
        this.batch = new SpriteBatch();

        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(1.5f);
    }


    /**
     * Отрисовывает элементы пользовательского интерфейса на экране.
     * Переводит матрицу отрисовки в систему координат камеры и выводит информацию
     * о текущей волне, а также статус боя или таймер обратного отсчета в зависимости от активности волны.
     */
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        WaveManager waveManager = gameLoop.getWaveManager();

        String waveText = "Wave: " + waveManager.getCurrentWaveNumber();
        String statusText;

        if (waveManager.isWaveActive()) {
            statusText = "Status: Battle in progress!";
            font.setColor(Color.RED);
        } else {
            statusText = String.format("Next wave: %.1f sec", waveManager.getTimeUntilNextWave());
            font.setColor(Color.GOLD);
        }

        font.draw(batch, waveText, 20, 460);
        font.draw(batch, statusText, 20, 435);

        batch.end();
    }

    /**
     * Освобождает ресурсы, используемые подсистемой интерфейса.
     * Принудительно очищает контекст {@link SpriteBatch} и удаляет шрифт {@link BitmapFont} из видеопамяти.
     */
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
