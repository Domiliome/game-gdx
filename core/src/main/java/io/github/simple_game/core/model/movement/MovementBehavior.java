package io.github.simple_game.core.model.movement;

import io.github.simple_game.core.model.entity.Enemy;

public interface MovementBehavior {
    void move(Enemy enemy, float deltaTime);
}
