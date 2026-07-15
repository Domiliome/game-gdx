package io.github.simple_game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class PhysicsSystem {

    // ИСПРАВЛЕНО: Теперь принимает GameManager для учета уровня прокачки урона
    public static void checkBallCollisions(GameManager gameManager, RedBall redBall, Array<GreenBall> greenBalls) {
        boolean isTouched = Gdx.input.isTouched();

        for (GreenBall gb : greenBalls) {
            float dx = gb.getCenterX() - redBall.getCenterX();
            float dy = gb.getCenterY() - redBall.getCenterY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float minDistance = redBall.getCurrentRadius() + gb.getCurrentRadius();

            if (distance < minDistance) {
                if (distance == 0) continue;

                float nx = dx / distance;
                float ny = dy / distance;
                float overlap = minDistance - distance;

                if (isTouched) {
                    gb.setPosition(gb.getCenterX() + nx * overlap, gb.getCenterY() + ny * overlap);
                } else {
                    redBall.setPosition(redBall.getCenterX() - nx * overlap * 0.5f, redBall.getCenterY() - ny * overlap * 0.5f);
                    gb.setPosition(gb.getCenterX() + nx * overlap * 0.5f, gb.getCenterY() + ny * overlap * 0.5f);
                }

                float rvx = gb.getVx() - redBall.getVx();
                float rvy = gb.getVy() - redBall.getVy();
                float velAlongNormal = rvx * nx + rvy * ny;

                // Если шары движутся навстречу друг другу
                if (velAlongNormal < 0) {
                    float restitution = 0.8f;
                    float impulseScalar = -(1 + restitution) * velAlongNormal;

                    float redMass = redBall.getCurrentRadius();
                    float greenMass = gb.getCurrentRadius();
                    float totalMass = redMass + greenMass;

                    float impulseX = (impulseScalar * nx) / totalMass;
                    float impulseY = (impulseScalar * ny) / totalMass;

                    // Расчет урона на основе силы столкновения
                    float strikeForce = Math.abs(velAlongNormal);

                    // Урон наносится, только если удар был ощутимым (скорость больше 80 пикселей в секунду)
                    if (strikeForce > 80f) {
                        // Базовый расчет урона по врагу
                        float baseEnemyDamage = strikeForce * 0.04f;

                        // ИСПРАВЛЕНО: Множитель урона увеличивается на +25% за каждый уровень апгрейда из магазина
                        float damageMultiplier = 1f + (gameManager.getDamageLevel() - 1) * 0.25f;
                        float finalEnemyDamage = baseEnemyDamage * damageMultiplier;

                        gb.takeDamage(finalEnemyDamage);

                        // Красный шар получает ответный урон в момент соударения
                        float playerDamage = strikeForce * 0.02f;
                        redBall.takeDamage(playerDamage);

                        Gdx.app.log("COMBAT", "Collision! Enemy takes: " + finalEnemyDamage + " (x" + damageMultiplier + ") | Player takes: " + playerDamage);
                    }

                    if (!isTouched) {
                        redBall.setVx(redBall.getVx() - greenMass * impulseX);
                        redBall.setVy(redBall.getVy() - greenMass * impulseY);
                    }

                    gb.setVx(gb.getVx() + (isTouched ? redMass * impulseX * 2.0f : redMass * impulseX));
                    gb.setVy(gb.getVy() + (isTouched ? redMass * impulseY * 2.0f : redMass * impulseY));
                }
            }
        }
    }

    public static void checkGreenBallCollisions(Array<GreenBall> greenBalls) {
        for (int i = 0; i < greenBalls.size; i++) {
            for (int j = i + 1; j < greenBalls.size; j++) {
                GreenBall b1 = greenBalls.get(i);
                GreenBall b2 = greenBalls.get(j);

                float dx = b2.getCenterX() - b1.getCenterX();
                float dy = b2.getCenterY() - b1.getCenterY();
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float minDistance = b1.getCurrentRadius() + b2.getCurrentRadius();

                if (distance < minDistance) {
                    if (distance == 0) continue;

                    float nx = dx / distance;
                    float ny = dy / distance;
                    float overlap = minDistance - distance;

                    b1.setPosition(b1.getCenterX() - nx * overlap * 0.5f, b1.getCenterY() - ny * overlap * 0.5f);
                    b2.setPosition(b2.getCenterX() + nx * overlap * 0.5f, b2.getCenterY() + ny * overlap * 0.5f);

                    float rvx = b2.getVx() - b1.getVx();
                    float rvy = b2.getVy() - b1.getVy();
                    float velAlongNormal = rvx * nx + rvy * ny;

                    if (velAlongNormal < 0) {
                        float restitution = 0.9f;
                        float impulseScalar = -(1 + restitution) * velAlongNormal;
                        float totalMass = 2f;
                        float impulseX = (impulseScalar * nx) / totalMass;
                        float impulseY = (impulseScalar * ny) / totalMass;

                        b1.setVx(b1.getVx() - impulseX);
                        b1.setVy(b1.getVy() - impulseY);
                        b2.setVx(b2.getVx() + impulseX);
                        b2.setVy(b2.getVy() + impulseY);
                    }
                }
            }
        }
    }
}
