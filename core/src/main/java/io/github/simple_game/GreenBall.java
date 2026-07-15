package io.github.simple_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GreenBall {
    private float centerX;
    private float centerY;

    private float vx = 0f;
    private float vy = 0f;

    private final float FRICTION = 0.985f;
    private final float BOUNCE = 0.9f;

    // Крупный базовый радиус (размер увеличен в 2 раза)
    private final float BASE_RADIUS = 40f;

    private float hp = 100f;
    private float maxHp = 100f;

    private final Viewport viewport;
    private final float padding;

    private final Texture spritesheet;
    private final Animation<TextureRegion> ballAnimation;
    private float stateTime = 0f;

    public GreenBall(float startX, float startY, Viewport viewport, float padding) {
        this.centerX = startX;
        this.centerY = startY;
        this.viewport = viewport;
        this.padding = padding;

        // Загружаем созданную PNG-ленту кадров из папки assets
        this.spritesheet = new Texture("green_ball_sheet.png");

        // Точный расчет пропорций сетки 24x5 для идеальной прямоугольной нарезки
        int FRAME_COLS = 5;
        int FRAME_ROWS = 24;

        int frameWidth = spritesheet.getWidth() / FRAME_COLS;
        int frameHeight = spritesheet.getHeight() / FRAME_ROWS;

        TextureRegion[] animationFrames;

        if (frameWidth <= 0 || frameHeight <= 0) {
            Gdx.app.error("ERROR", "Критическая ошибка: файл green_ball_sheet.png имеет размер 0 или не найден!");
            animationFrames = new TextureRegion[]{ new TextureRegion(spritesheet, 0, 0, 1, 1) };
        } else {
            // Нарезаем спрайтшит на четкие прямоугольные секции
            TextureRegion[][] tmp = TextureRegion.split(spritesheet, frameWidth, frameHeight);

            int rows = tmp.length;
            int cols = (tmp.length > 0) ? tmp[0].length : 0;
            int totalFrames = rows * cols;

            // Последовательно собираем все 120 кадров из таблицы в один массив
            animationFrames = new TextureRegion[totalFrames];
            int index = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    animationFrames[index++] = tmp[i][j];
                }
            }
        }

        // ИСПРАВЛЕНО: Скорость анимации ускорена в два раза (интервал уменьшен с 0.06f до 0.03f)
        this.ballAnimation = new Animation<TextureRegion>(0.03f, animationFrames);
        this.ballAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float step) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime; // Обновляем таймер анимации для смены кадров

        // Физика качения шара
        centerX += vx * step;
        centerY += vy * step;

        vx *= Math.pow(FRICTION, step * 60);
        vy *= Math.pow(FRICTION, step * 60);

        if (Math.abs(vx) < 5f) vx = 0f;
        if (Math.abs(vy) < 5f) vy = 0f;

        // Отскоки от стен (используют стабильный базовый радиус 40f)
        float minX = padding + BASE_RADIUS;
        float maxX = viewport.getWorldWidth() - padding - BASE_RADIUS;
        if (centerX < minX) { centerX = minX; vx = -vx * BOUNCE; }
        else if (centerX > maxX) { centerX = maxX; vx = -vx * BOUNCE; }

        float minY = padding + BASE_RADIUS;
        float maxY = viewport.getWorldHeight() - padding - BASE_RADIUS;
        if (centerY < minY) { centerY = minY; vy = -vy * BOUNCE; }
        else if (centerY > maxY) { centerY = maxY; vy = -vy * BOUNCE; }
    }

    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        float diameter = BASE_RADIUS * 2f;
        float drawX = centerX - BASE_RADIUS;
        float drawY = centerY - BASE_RADIUS;

        TextureRegion currentFrame = ballAnimation.getKeyFrame(stateTime, true);

        if (currentFrame != null) {
            batch.draw(currentFrame, drawX, drawY, diameter, diameter);
        }

        // Отрисовка полоски здоровья на фиксированной высоте над шаром
        if (hp < maxHp && hp > 0f) {
            batch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            float barWidth = BASE_RADIUS * 2f;
            float barHeight = 4f;
            float barX = centerX - (barWidth / 2f);
            float barY = centerY + BASE_RADIUS + 8f;
            float hpPercent = hp / maxHp;

            shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1f);
            shapeRenderer.rect(barX, barY, barWidth, barHeight);
            shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1f);
            shapeRenderer.rect(barX, barY, barWidth * hpPercent, barHeight);
            shapeRenderer.end();
            batch.begin();
        }
    }

    public void resetDrag() {}

    public void dispose() {
        if (spritesheet != null) {
            spritesheet.dispose();
        }
    }

    public void setMaxHp(float maxHp) { this.maxHp = maxHp; }
    public void heal(float amount) {
        this.hp += amount;
        if (this.hp > maxHp) this.hp = maxHp;
    }
    public void takeDamage(float damage) {
        this.hp -= damage;
        if (this.hp < 0f) this.hp = 0f;
    }
    public boolean isDead() { return this.hp <= 0f; }

    // === ГЕТТЕРЫ И СЕТТЕРЫ ===
    public float getHp() { return hp; }
    public float getMaxHp() { return maxHp; }
    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }
    public float getCurrentRadius() { return BASE_RADIUS; }
    public float getVx() { return vx; }
    public float getVy() { return vy; }
    public void setVx(float vx) { this.vx = vx; }
    public void setVy(float vy) { this.vy = vy; }
    public void setPosition(float x, float y) { this.centerX = x; this.centerY = y; }
}
