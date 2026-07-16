package io.github.simple_game.core;

import com.badlogic.gdx.Game;

import io.github.simple_game.core.presentation.screen.GameScreen;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
