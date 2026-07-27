package io.github.simple_game.core.service;

import com.badlogic.gdx.math.Vector2;
import io.github.simple_game.core.model.entity.Enemy;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.model.movement.WalkMovement;

/**
 * Фабрика для инкапсуляции логики генерации и баланса характеристик врагов.
 */
public class EnemyFactory {

    public static Enemy createEnemy(int waveNumber, RoadPath roadPath) {
        // Получаем точку старта
        Vector2 startPoint = roadPath.getPoint(0);

        // Ваша оригинальная математическая прогрессия характеристик
        float health = 80f + (waveNumber * 20f);
        float speed = 70f + Math.min(50f, waveNumber * 5f);

        // Создаем классического наземного врага
        return new Enemy(startPoint.x, startPoint.y, health, speed, new WalkMovement(roadPath));
    }
}
