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
import io.github.simple_game.core.service.GameLoop;

public class TowerControlPanel extends Table {
    private final GameLoop gameLoop;
    private final TextButton upgradeBtn, sellBtn;
    private final Texture upBg, sellBg;

    public TowerControlPanel(GameLoop gameLoop) {
        this.gameLoop = gameLoop;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.1f, 0.4f, 0.8f, 0.9f)); pixmap.fill();
        this.upBg = new Texture(pixmap);
        pixmap.setColor(new Color(0.7f, 0.1f, 0.1f, 0.9f)); pixmap.fill();
        this.sellBg = new Texture(pixmap);
        pixmap.dispose();

        TextButton.TextButtonStyle upStyle = new TextButton.TextButtonStyle();
        upStyle.font = new BitmapFont(); upStyle.font.getData().setScale(1.25f);
        upStyle.fontColor = Color.WHITE; upStyle.up = new TextureRegionDrawable(upBg);

        TextButton.TextButtonStyle sellStyle = new TextButton.TextButtonStyle();
        sellStyle.font = new BitmapFont(); sellStyle.font.getData().setScale(1.25f);
        sellStyle.fontColor = Color.WHITE; sellStyle.up = new TextureRegionDrawable(sellBg);

        upgradeBtn = new TextButton(" UPGRADE ", upStyle);
        upgradeBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                Tower sel = gameLoop.getSelectedTower();
                if (sel == null || !sel.canUpgrade()) {
                    return;
                }
                int cost = sel.getUpgradeCost();
                if (gameLoop.getCurrencyManager().spendGold(cost)) {
                    sel.addInvestedGold(cost);
                    sel.tryUpgrade();
                }
            }
        });

        sellBtn = new TextButton(" SELL ", sellStyle);
        sellBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                gameLoop.sellSelectedTower();
            }
        });


        this.center();
        this.add(upgradeBtn).size(200, 56).padRight(12);
        this.add(sellBtn).size(200, 56);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        Tower sel = gameLoop.getSelectedTower();

        if (sel == null) {
            this.setVisible(false);
            return;
        }

        this.setVisible(true);
        if (sel.canUpgrade()) {
            upgradeBtn.setText("UPGRADE: " + sel.getUpgradeCost() + "G");
            int currentGold = gameLoop.getCurrencyManager().getGold();
            upgradeBtn.getLabel().setColor(currentGold >= sel.getUpgradeCost() ? Color.WHITE : Color.RED);
            upgradeBtn.setDisabled(false);
        } else {
            upgradeBtn.setText("MAX LEVEL");
            upgradeBtn.getLabel().setColor(Color.GRAY);
            upgradeBtn.setDisabled(true);
        }

        sellBtn.setText("SELL: +" + sel.getSellRefund() + "G");
    }

    public void dispose() {
        if (upBg != null) upBg.dispose();
        if (sellBg != null) sellBg.dispose();
    }
}
