package io.github.simple_game.core.service;

/**
 * Менеджер экономики игрока (Управление внутриигровой валютой и жизнями).
 * Отвечает за хранение текущего баланса золота, количество оставшихся жизней базы,
 * списание средств при покупке/апгрейде и начисление наград за уничтожение врагов.
 */
public class CurrencyManager {
    private int gold;
    private int lives;

    /**
     * Создает менеджер экономики со стартовыми значениями для текущего уровня.
     *
     * @param startingGold  начальное количество золота у игрока (например, 300)
     * @param startingLives начальное количество жизней базы (например, 20)
     */
    public CurrencyManager(int startingGold, int startingLives) {
        this.gold = startingGold;
        this.lives = startingLives;
    }

    /**
     * Начисляет золото на баланс игрока (например, за убийство моба или завершение волны).
     *
     * @param amount количество добавляемого золота
     */
    public void addGold(int amount) {
        if (amount > 0) {
            this.gold += amount;
            System.out.println("Получено золото: +" + amount + ". Текущий баланс: " + this.gold);
        }
    }

    /**
     * Пытается списать золото при покупке или улучшении.
     *
     * @param amount стоимость операции в золотых монетах
     * @return true, если золота хватило и оно успешно списано; false, если средств недостаточно
     */
    public boolean spendGold(int amount) {
        if (amount <= 0) return false;

        if (this.gold >= amount) {
            this.gold -= amount;
            System.out.println("Потрачено золото: -" + amount + ". Текущий баланс: " + this.gold);
            return true;
        }
        System.out.println("Недостаточно золота! Требуется: " + amount + ", в наличии: " + this.gold);
        return false;
    }

    /**
     * Отнимает жизни у базы, когда враг успешно добирается до конца маршрута.
     *
     * @param amount количество отнимаемых жизней (обычно 1)
     */
    public void decreaseLives(int amount) {
        if (amount > 0) {
            this.lives = Math.max(0, this.lives - amount);
            System.out.println("База атакована! Потеряно жизней: -" + amount + ". Осталось жизней: " + this.lives);

            if (isGameOver()) {
                System.out.println("ИГРА ОКОНЧЕНА! База уничтожена.");
                // В будущем здесь будет вызов экрана Game Over
            }
        }
    }

    /**
     * Проверяет, потерял ли игрок все жизни (условие поражения).
     *
     * @return true, если жизней не осталось; иначе false
     */
    public boolean isGameOver() {
        return this.lives <= 0;
    }

    /** @return текущее количество золота у игрока */
    public int getGold() { return gold; }

    /** @return текущее количество жизней базы */
    public int getLives() { return lives; }
}
