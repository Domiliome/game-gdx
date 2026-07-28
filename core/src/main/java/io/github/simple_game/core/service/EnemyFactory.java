package io.github.simple_game.core.service;

import com.badlogic.gdx.math.Vector2;
import io.github.simple_game.core.model.entity.enemy.Enemy;
import io.github.simple_game.core.model.entity.enemy.EnemyTier;
import io.github.simple_game.core.model.entity.enemy.FastGoblin;
import io.github.simple_game.core.model.entity.enemy.HeavyOrc;
import io.github.simple_game.core.model.entity.enemy.NormalZombie;
import io.github.simple_game.core.model.movement.RoadPath;
import io.github.simple_game.core.model.movement.WalkMovement;

/**
 * Фабрика для инкапсуляции логики генерации и баланса характеристик разных типов врагов.
 */
public class EnemyFactory {

    public static Enemy createEnemy(EnemyTier tier, int waveNumber, RoadPath roadPath) {
        // Получаем точку старта
        Vector2 startPoint = roadPath.getPoint(0);

        // Общая математическая прогрессия сложности от номера волны (+10% за раунд)
        float hpMod = 1f + (waveNumber - 1) * 0.10f;
        float speedMod = 1f + Math.min(0.4f, (waveNumber - 1) * 0.03f);

        // Полиморфно создаем конкретный подкласс врага под выбранный тир сложности
        Enemy enemy = switch (tier) {
            case TIER_1_LIGHT  -> new FastGoblin(startPoint.x, startPoint.y, hpMod, speedMod);
            case TIER_2_NORMAL -> new NormalZombie(startPoint.x, startPoint.y, hpMod, speedMod);
            case TIER_3_HEAVY  -> new HeavyOrc(startPoint.x, startPoint.y, hpMod, speedMod);
        };

        // Навешиваем уникальную стратегию движения WalkMovement для каждого отдельного инстанса врага
        enemy.setMovementBehavior(new WalkMovement(roadPath));
        return enemy;
    }
}
