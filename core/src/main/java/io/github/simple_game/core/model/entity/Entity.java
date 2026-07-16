package io.github.simple_game.core.model.entity;

import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 position;

    public Entity(float x, float y) {
        this.position = new Vector2(x, y);
    }

    // Метод обновления логики (вызывается каждый кадр из GameLoop)
    public abstract void update(float deltaTime);

    public Vector2 getPosition() {
        return position;
    }
}
