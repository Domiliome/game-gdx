package io.github.simple_game.core.model.entity.tower;

import java.util.Locale;

import io.github.simple_game.core.service.GameLoop;

/**
 * Каталог башен: одна запись = тип в магазине + текстура + скорость снаряда + фабрика.
 * Чтобы добавить башню: создай класс и добавь константу сюда.
 */
public enum TowerType {
    ARCHER(100, "towers/archer.png", 400f, ArcherTower::new),
    CANNON(250, "towers/cannon.png", 250f, CannonTower::new),
    MAGIC(200, "towers/magic.png", 320f, MagicTower::new),
    POISON(175, "towers/poison.png", 280f, PoisonTower::new),
    /** Два столба в радиусе связи образуют молнию между собой. */
    TESLA(150, "towers/tesla.png", 0f, TeslaTower::new);

    private final int cost;
    private final String idleTexturePath;
    private final float projectileSpeed;
    private final TowerFactory factory;

    TowerType(int cost, String idleTexturePath, float projectileSpeed, TowerFactory factory) {
        this.cost = cost;
        this.idleTexturePath = idleTexturePath;
        this.projectileSpeed = projectileSpeed;
        this.factory = factory;
    }

    public int getCost() {
        return cost;
    }

    public String getIdleTexturePath() {
        return idleTexturePath;
    }

    public String getCardTexturePath() {
        return "card/" + name().toLowerCase(Locale.ROOT) + "_card.png";
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    public Tower create(float x, float y, GameLoop gameLoop) {
        return factory.create(x, y, gameLoop);
    }
}
