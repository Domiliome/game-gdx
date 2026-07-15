package io.github.simple_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameManager {
    private final RedBall redBall;
    private final Array<GreenBall> greenBalls;
    private final Viewport viewport;
    private final float padding;

    private int money = 0;

    private final Preferences prefs;
    private static final String PREFS_NAME = "simple_game_settings";
    private static final String KEY_MONEY = "player_money";

    private static final String KEY_DAMAGE_LVL = "player_damage_lvl";
    private static final String KEY_HP_LVL = "player_hp_lvl";

    private int damageLevel = 1;
    private int hpLevel = 1;

    private int currentWave = 1;

    private final float worldWidth;
    private final float worldHeight;

    public GameManager(Viewport viewport, float padding, float worldWidth, float worldHeight) {
        this.viewport = viewport;
        this.padding = padding;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        this.prefs = Gdx.app.getPreferences(PREFS_NAME);
        this.money = prefs.getInteger(KEY_MONEY, 0);

        this.damageLevel = prefs.getInteger(KEY_DAMAGE_LVL, 1);
        this.hpLevel = prefs.getInteger(KEY_HP_LVL, 1);

        this.redBall = new RedBall(worldWidth / 2f, worldHeight / 4f, viewport, padding);

        applyUpgradesToPlayer();

        this.greenBalls = new Array<>();

        spawnGreenBalls(3);
    }

    private void applyUpgradesToPlayer() {
        float maxHp = 200f + (hpLevel - 1) * 50f;
        redBall.setMaxHp(maxHp);
        redBall.heal(maxHp);
    }

    public void saveProgress() {
        prefs.putInteger(KEY_MONEY, money);
        prefs.putInteger(KEY_DAMAGE_LVL, damageLevel);
        prefs.putInteger(KEY_HP_LVL, hpLevel);
        prefs.flush();
    }

    public void buyDamageUpgrade() {
        int cost = getDamageCost();
        if (money >= cost) {
            money -= cost;
            damageLevel++;
            saveProgress();
            Gdx.app.log("SHOP", "Куплен апгрейд атаки! Текущий уровень: " + damageLevel);
        }
    }

    public void buyHpUpgrade() {
        int cost = getHpCost();
        if (money >= cost) {
            money -= cost;
            hpLevel++;
            applyUpgradesToPlayer();
            saveProgress();
            Gdx.app.log("SHOP", "Куплен апгрейд здоровья! Текущий уровень: " + hpLevel);
        }
    }

    public void spawnGreenBalls(int count) {
        for (int i = 0; i < count; i++) {
            float randomX = MathUtils.random(padding + 30f, worldWidth - padding - 30f);
            float minY = (worldHeight * 0.4f) + 50f;
            float randomY = MathUtils.random(minY, worldHeight - padding - 50f);

            GreenBall gb = new GreenBall(randomX, randomY, viewport, padding);

            float waveHpModifier = 1f + (currentWave - 1) * 0.25f;
            float targetHp = 100f * waveHpModifier;

            gb.setMaxHp(targetHp);
            gb.heal(targetHp);

            greenBalls.add(gb);
        }
    }

    // ИСПРАВЛЕНО: Интегрирован ИИ плавного преследования игрока зелеными шарами
     public void updatePhysics(float step) {
        // ДОБАВЛЕНО: Железная защита от вылета.
        // Если красный шар игрока еще не создан или уничтожен, останавливаем шаг физики
        if (redBall == null) return;

        if (redBall.isDead()) {
            gameOverReset();
            return;
        }

        redBall.update(step);

        // Координаты цели (красного шара)
        float targetX = redBall.getCenterX();
        float targetY = redBall.getCenterY();

        for (GreenBall gb : greenBalls) {
            // ДОБАВЛЕНО: Защита на случай, если зеленый шар еще создается в памяти
            if (gb == null) continue;

            // Считаем вектор расстояния и направления до игрока
            float dx = targetX - gb.getCenterX();
            float dy = targetY - gb.getCenterY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > 0f) {
                float dirX = dx / distance;
                float dirY = dy / distance;

                // Сила тяги ИИ: базовая скорость 200 пикселей/сек + 15% за раунд волны
                float waveSpeedModifier = 1f + (currentWave - 1) * 0.15f;
                float chaseForce = 200f * waveSpeedModifier;

                // Прикладываем ускорение к текущей скорости вектора качения
                gb.setVx(gb.getVx() + dirX * chaseForce * step);
                gb.setVy(gb.getVy() + dirY * chaseForce * step);
            }

            gb.update(step);
        }

        PhysicsSystem.checkBallCollisions(this, redBall, greenBalls);
        PhysicsSystem.checkGreenBallCollisions(greenBalls);

        checkDeadGreenBalls();

        if (greenBalls.size == 0) {
            currentWave++;
            Gdx.app.log("WAVE", "Все враги повержены! Начинается волна №" + currentWave);

            int enemiesToSpawn = Math.min(6, 3 + (currentWave / 2));
            spawnGreenBalls(enemiesToSpawn);
        }
    }


    private void checkDeadGreenBalls() {
        for (int i = greenBalls.size - 1; i >= 0; i--) {
            GreenBall gb = greenBalls.get(i);

            if (gb.isDead()) {
                int reward = 25 + (currentWave - 1) * 5;
                money += reward;

                Gdx.app.log("GAME", "Green ball destroyed! Earned: +" + reward + " Coins. Total: " + money);

                redBall.heal(20f);

                saveProgress();
                greenBalls.removeIndex(i);
            }
        }
    }

    private void gameOverReset() {
        Gdx.app.log("GAME", "Player Died! Resetting combat arena...");

        money = (int) (money * 0.90f);
        saveProgress();

        currentWave = 1;

        redBall.setPosition(worldWidth / 2f, worldHeight / 4f);
        redBall.setVx(0f);
        redBall.setVy(0f);

        float maxHp = 200f + (hpLevel - 1) * 50f;
        redBall.heal(maxHp);

        greenBalls.clear();
        spawnGreenBalls(3);
    }

    public int getCurrentWave() { return currentWave; }
    public int getDamageLevel() { return damageLevel; }
    public int getHpLevel() { return hpLevel; }
    public int getDamageCost() { return damageLevel * 100; }
    public int getHpCost() { return hpLevel * 100; }

    public RedBall getRedBall() { return redBall; }
    public Array<GreenBall> getGreenBalls() { return greenBalls; }
    public int getMoney() { return money; }

    public Object getShopZone() { return null; }
}
