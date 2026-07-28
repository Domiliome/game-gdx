package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.service.CurrencyManager;
import io.github.simple_game.core.service.GameLoop;

public class UpgradeButton extends Table {
    private final GameLoop gameLoop;
    private final TextButton upgradeBtn;
    private final Texture btnBg;

    public UpgradeButton(GameLoop gameLoop) {
        this.gameLoop = gameLoop;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.1f, 0.4f, 0.8f, 0.9f)); // Синяя кнопка апгрейда
        pixmap.fill();
        this.btnBg = new Texture(pixmap);
        pixmap.dispose();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = new BitmapFont();
        style.font.getData().setScale(2.0f); // Крупный текст для удобного тапа
        style.fontColor = Color.WHITE;
        style.up = new TextureRegionDrawable(btnBg);

        upgradeBtn = new TextButton(" UPGRADE ", style);
        upgradeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Tower selected = gameLoop.getSelectedTower();
                CurrencyManager economy = gameLoop.getCurrencyManager();
                // Если башня выбрана и хватает золота — улучшаем её!
                if (selected != null && economy.spendGold(selected.getUpgradeCost())) {
                    selected.tryUpgrade();
                }
            }
        });

        this.center();
        this.add(upgradeBtn).size(300, 70);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Tower selected = gameLoop.getSelectedTower();

        // Если ни одна башня не выбрана — полностью скрываем кнопку с экрана
        if (selected == null) {
            this.setVisible(false);
            return;
        }

        // Если башня выбрана — показываем кнопку и динамически пишем цену апгрейда
        this.setVisible(true);
        upgradeBtn.setText(" UPGRADE: " + selected.getUpgradeCost() + "G ");

        // Перекрашиваем текст кнопки в красный, если у игрока не хватает золота
        int currentGold = gameLoop.getCurrencyManager().getGold();
        upgradeBtn.getLabel().setColor(currentGold >= selected.getUpgradeCost() ? Color.WHITE : Color.RED);
    }
}
