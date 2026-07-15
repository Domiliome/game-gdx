package io.github.simple_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameManager {
    private final RedBall redBall;
    private final Array<GreenBall> greenBalls;
    private final ShopZone shopZone;
    private final Viewport viewport;
    private final float padding;

    private int money = 0;

    public GameManager(Viewport viewport, float padding, float worldWidth, float worldHeight) {
        this.viewport = viewport;
        this.padding = padding;

        this.redBall = new RedBall(worldWidth / 2f, worldHeight / 4f, viewport, padding);
        this.shopZone = new ShopZone(padding, 80f);
        this.greenBalls = new Array<>();

        spawnGreenBalls(3);
    }

    public void spawnGreenBalls(int count) {
        for (int i = 0; i < count; i++) {
            float randomX = MathUtils.random(padding + 30f, viewport.getWorldWidth() - padding - 30f);
            float minY = (viewport.getWorldHeight() * 0.4f) + 50f;
            float randomY = MathUtils.random(minY, viewport.getWorldHeight() - padding - 50f);

            greenBalls.add(new GreenBall(randomX, randomY, viewport, padding));
        }
    }

    public void updatePhysics(float step) {
        redBall.update(step);

        for (GreenBall gb : greenBalls) {
            gb.update(step);
        }

        // Вызываем статические методы физического движка
        PhysicsSystem.checkBallCollisions(redBall, greenBalls);
        PhysicsSystem.checkGreenBallCollisions(greenBalls);
        checkShopZoneCollision();

        if (greenBalls.size <= 1) {
            spawnGreenBalls(2);
        }
    }

    private void checkShopZoneCollision() {
        for (int i = greenBalls.size - 1; i >= 0; i--) {
            GreenBall gb = greenBalls.get(i);

            if (shopZone.checkCollision(gb)) {
                if (gb.getCenterY() < viewport.getWorldHeight() * 0.4f) {
                    money += 10;
                    Gdx.app.log("GAME", "Green ball sold! Coins: " + money);
                    greenBalls.removeIndex(i);
                }
            }
        }
    }

    // Геттеры для отрисовки в главном классе
    public RedBall getRedBall() { return redBall; }
    public Array<GreenBall> getGreenBalls() { return greenBalls; }
    public ShopZone getShopZone() { return shopZone; }
    public int getMoney() { return money; }
}
