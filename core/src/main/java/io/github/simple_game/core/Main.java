package io.github.simple_game.core;

import com.badlogic.gdx.Game;

import io.github.simple_game.core.presentation.screen.GameScreen;

/**
 * Главный управляющий класс приложения (Точка входа кроссплатформенной логики).
 * Наследуется от класса {@link Game} фреймворка LibGDX. Отвечает за инициализацию
 * базовых систем игры при старте и делегирует управление активными экранами
 * (например, переключение между главным меню и игровым экраном).
 */
public class Main extends Game {

    /**
     * Вызывается автоматически фреймворком в момент запуска приложения на целевой платформе.
     * Устанавливает основной игровой экран {@link GameScreen} в качестве текущего активного окна.
     */
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
