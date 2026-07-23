package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.simple_game.core.service.CurrencyManager;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.WaveManager;

/**
 * Класс, отвечающий за отрисовку пользовательского интерфейса (UI) поверх игрового экрана.
 * Использует {@link BitmapFont} для рендеринга текстовой информации, получая актуальные данные
 * о состоянии волн из {@link WaveManager} и показателях экономики из {@link CurrencyManager}.
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
     * Переводит матрицу отрисовки в систему координат камеры и последовательно выводит информацию
     * о текущей волне, статусе боя, а также о количестве золота и жизней игрока в верхней части экрана.
     */
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        WaveManager waveManager = gameLoop.getWaveManager();
        CurrencyManager economy = gameLoop.getCurrencyManager();

        String waveText = "Wave: " + waveManager.getCurrentWaveNumber();
        String statusText;

        if (waveManager.isWaveActive()) {
            statusText = "Status: Battle in progress!";
            font.setColor(Color.RED);
        } else {
            statusText = String.format("Next wave: %.1f sec", waveManager.getTimeUntilNextWave());
            font.setColor(Color.GOLD);
        }

        // Выводим информацию о волне и статусе в верхней левой части вертикального экрана
        font.draw(batch, waveText, 20, 780);
        font.draw(batch, statusText, 20, 755);

        // Формируем и выводим строку экономики зеленым цветом чуть ниже
        String economyText = "Gold: " + economy.getGold() + "  |  Lives: " + economy.getLives();
        font.setColor(Color.GREEN);
        font.draw(batch, economyText, 20, 720);

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
