package io.github.simple_game.core.model.movement;

/**
 * Типы процедурной генерации дорожного коридора.
 */
public enum PathType {
    STRAIGHT_FEW_TURNS, // Прямая дорога с 2-4 крупными поворотами
    MANY_TURNS,         // Сложный "серпантин" с большим количеством зигзагов
    WITH_LOOPS          // Дорога с петлями/кольцами (закольцованный обход центра)
}
