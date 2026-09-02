package io.github.simple_game.core.presentation.input;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.simple_game.core.model.entity.map.GameGrid;

public class CameraGestureService extends GestureDetector.GestureAdapter {
    private final OrthographicCamera camera;
    private final Vector2 velocity = new Vector2();
    private float initialZoom = 1.0f;

    private static final float PAN_SENSITIVITY = 0.6f;
    private static final float FRICTION = 0.90f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 2.0f;

    public CameraGestureService(Viewport worldViewport) {
        this.camera = (OrthographicCamera) worldViewport.getCamera();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        velocity.set(0, 0);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.position.add(-deltaX * camera.zoom * PAN_SENSITIVITY, deltaY * camera.zoom * PAN_SENSITIVITY, 0);
        clampCamera();
        return true;
    }

    @Override
    public boolean fling(float vx, float vy, int button) {
        velocity.set(-vx * camera.zoom * PAN_SENSITIVITY, vy * camera.zoom * PAN_SENSITIVITY);
        return true;
    }

    public void updateInertia(float deltaTime) {
        if (velocity.len() < 10f) { velocity.set(0, 0); return; }
        camera.position.add(velocity.x * deltaTime, velocity.y * deltaTime, 0);
        velocity.scl((float) Math.pow(FRICTION, deltaTime * 60f));
        clampCamera();
    }

    @Override
    public boolean pinch(Vector2 init1, Vector2 init2, Vector2 p1, Vector2 p2) {
        float initialDistance = init1.dst(init2);
        float currentDistance = p1.dst(p2);
        if (initialDistance == 0) return false;

        camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, initialZoom * (initialDistance / currentDistance)));
        clampCamera();
        return true;
    }

    @Override
    public void pinchStop() {
        initialZoom = camera.zoom;
    }

    public void clampToWorld() {
        clampCamera();
        initialZoom = camera.zoom;
    }

    private void clampCamera() {
        float worldW = GameGrid.worldWidth();
        float worldH = GameGrid.worldHeight();
        float maxZoom = Math.min(worldW / camera.viewportWidth, worldH / camera.viewportHeight);
        camera.zoom = Math.max(MIN_ZOOM, Math.min(camera.zoom, Math.min(MAX_ZOOM, maxZoom)));

        float halfW = (camera.viewportWidth * camera.zoom) / 2f;
        float halfH = (camera.viewportHeight * camera.zoom) / 2f;

        camera.position.x = Math.max(halfW, Math.min(worldW - halfW, camera.position.x));
        camera.position.y = Math.max(halfH, Math.min(worldH - halfH, camera.position.y));
    }
}
