package io.github.simple_game.core.model.entity.tower;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.projectile.CannonBall;
import io.github.simple_game.core.model.entity.projectile.Projectile;
import io.github.simple_game.core.service.GameLoop;

/**
 * Артиллерийская пушка. Обладает сокрушительным разовым уроном.
 * Полностью контролирует свои боевые характеристики, логику улучшений
 * и выпускает тяжелые ядра, наносящие взрывной урон по площади.
 */
public class CannonTower extends Tower {

    // Стартовые базовые параметры пушки на первом уровне
    private static final float BASE_DAMAGE = 50f;
    private static final float BASE_RANGE = 120f;
    private static final float BASE_COOLDOWN = 2.0f;

    // Уникальные коэффициенты прокачки только для Артиллерийской пушки
    private final float damageMultiplier = 1.45f;   // Огромный прирост урона (+45% за каждый уровень)
    private final float rangeMultiplier = 1.08f;    // Небольшой прирост дальности
    private final float cooldownReduction = 1.0f;   // Скорость атаки не растет вообще

    /**
     * Создает новую артиллерийскую башню в заданных координатах.
     */
    public CannonTower(float x, float y, GameLoop gameLoop) {
        super(x, y, TowerType.CANNON, gameLoop);
        this.damage = BASE_DAMAGE;
        this.attackRange = BASE_RANGE;
        this.attackCooldown = BASE_COOLDOWN;

        // ВНЕДРЕНИЕ АНИМАЦИИ: Загружаем 12-кадровую ленту Horizontal Strip для пушки
        Texture sheet = new Texture(Gdx.files.internal("tower_cannon_init.png"));
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);

        // Автоматически считываем длину горизонтального ряда кадров
        int totalFrames = tmp[0].length;
        TextureRegion[] animationFrames = new TextureRegion[totalFrames];

        // ИСПРАВЛЕНО: Заменили ручной цикл копирования на системный arraycopy. Предупреждение исчезнет!
        System.arraycopy(tmp[0], 0, animationFrames, 0, totalFrames);

        // ИСПРАВЛЕНО: Убрали избыточный тип данных в выражении new, применив оператор-алмаз <>
        this.initAnimation = new Animation<>(0.06f, animationFrames);
    }

    /**
     * Повышает уровень артиллерийской башни до maximal предела.
     */
    @Override
    public void tryUpgrade() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            this.damage = BASE_DAMAGE * (float) Math.pow(damageMultiplier, currentLevel - 1);
            this.attackRange = BASE_RANGE * (float) Math.pow(rangeMultiplier, currentLevel - 1);
            this.attackCooldown = Math.max(0.1f, BASE_COOLDOWN * (float) Math.pow(cooldownReduction, currentLevel - 1));

            // Синхронизируем базовые характеристики для баффов от предметов из рюкзака
            this.baseDamage = this.damage;
            this.baseAttackRange = this.attackRange;
            this.baseAttackCooldown = this.attackCooldown;

            System.out.println("Пушка улучшена до уровня " + currentLevel);
        }
    }

    /**
     * Возвращает стоимость улучшения для артиллерийской башни.
     */
    @Override
    public int getUpgradeCost() {
        return (int) (type.getCost() * 0.7f * currentLevel);
    }

    /**
     * Производит выстрел по текущей установленной цели.
     */
    @Override
    protected void shoot(Array<Projectile> projectilesToSpawn) {
        // Спавним специализированное тяжелое ядро пушки, передавая в него игровой цикл для Splash-урона
        Projectile cannonBall = new CannonBall(position.x, position.y, target, damage, type, gameLoop);
        projectilesToSpawn.add(cannonBall);
        System.out.println("Пушка бабахнула! Нанесено " + damage + " ед. урона по площади");
    }

    // Универсальные геттеры для вызова внутри WorldSpriteRenderer
    public TextureRegion getCurrentInitFrame() {
        return initAnimation != null ? initAnimation.getKeyFrame(animationTime) : null;
    }

    public boolean isInitializing() {
        return isInitializing;
    }
}
