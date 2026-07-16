package io.github.simple_game.core.model.movement;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class RoadPath {
    private final Array<Vector2> points;

    public RoadPath() {
        points = new Array<>();
    }

    public void addPoint(float x, float y) {
        points.add(new Vector2(x, y));
    }

    public Vector2 getPoint(int index) {
        return points.get(index);
    }

    public int getPointCount() {
        return points.size;
    }
}
