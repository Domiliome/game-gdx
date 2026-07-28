package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.simple_game.core.model.entity.tower.TowerType;
import io.github.simple_game.core.presentation.view.GameRenderer;
import io.github.simple_game.core.service.DragAndDropManager;
import io.github.simple_game.core.service.GameLoop;

public class ShopPanel extends Table {
    private final GameLoop gameLoop;
    private final DragAndDropManager dragManager;
    private final Texture backgroundTexture;
    private final Label priceLabel1, priceLabel2, priceLabel3;
    private final float scale;

    public ShopPanel(GameLoop gameLoop, GameRenderer renderer, DragAndDropManager dragManager) {
        this.gameLoop = gameLoop;
        this.dragManager = dragManager;

        // Определяем платформу (2.0f для Android, 1.0f для десктопа)
        this.scale = (Gdx.app.getType() == Application.ApplicationType.Android) ? 2.0f : 1.0f;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.12f, 0.12f, 0.12f, 1f));
        pixmap.fill();
        this.backgroundTexture = new Texture(pixmap);
        pixmap.dispose();
        this.setBackground(new TextureRegionDrawable(backgroundTexture));

        Label.LabelStyle priceStyle = new Label.LabelStyle(new BitmapFont(), Color.GOLD);
        priceLabel1 = new Label("100G", priceStyle);
        priceLabel2 = new Label("250G", priceStyle);
        priceLabel3 = new Label("200G", priceStyle);

        Table slot1 = createShopSlot(new Image(renderer.getArcherTowerTexture()), priceLabel1, TowerType.ARCHER);
        Table slot2 = createShopSlot(new Image(renderer.getCannonTowerTexture()), priceLabel2, TowerType.CANNON);
        Table slot3 = createShopSlot(new Image(renderer.getMagicTowerTexture()),  priceLabel3, TowerType.MAGIC);

        this.bottom().center();
        // Адаптивные отступы (10/15 пикселей для ПК, 20/30 для Android)
        this.add(slot1).expandX().fillY().padTop(10 * scale).padBottom(15 * scale);
        this.add(slot2).expandX().fillY().padTop(10 * scale).padBottom(15 * scale);
        this.add(slot3).expandX().fillY().padTop(10 * scale).padBottom(15 * scale);
    }

    private Table createShopSlot(Image towerImage, Label priceLabel, final TowerType towerType) {
        Table slotTable = new Table();
        priceLabel.getStyle().font.getData().setScale(1.2f * scale); // 1.2f на ПК, 2.4f на телефоне

        // Адаптивный размер иконок (64x64 на ПК, 128x128 на Android)
        slotTable.add(towerImage).size(64 * scale, 64 * scale).padBottom(5 * scale);
        slotTable.row();
        slotTable.add(priceLabel);

        DragListener listener = new DragListener() {
            private boolean isDraggingActive = false;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                if (dragManager == null) return;
                dragManager.startDrag(towerType, Gdx.input.getX(), Gdx.input.getY());
                isDraggingActive = true;
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (!isDraggingActive || dragManager == null) return;
                dragManager.updatePosition(Gdx.input.getX(), Gdx.input.getY());
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (!isDraggingActive || dragManager == null) return;
                dragManager.stopDragAndPlace();
                isDraggingActive = false;
            }
        };

        listener.setTapSquareSize(1f);
        slotTable.addListener(listener);
        return slotTable;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        int currentGold = gameLoop.getCurrencyManager().getGold();
        priceLabel1.setColor(currentGold >= TowerType.ARCHER.getCost() ? Color.GOLD : Color.RED);
        priceLabel2.setColor(currentGold >= TowerType.CANNON.getCost() ? Color.GOLD : Color.RED);
        priceLabel3.setColor(currentGold >= TowerType.MAGIC.getCost()  ? Color.GOLD : Color.RED);
    }

    @Override
    public float getPrefWidth() { return Gdx.graphics.getWidth(); }

    public void dispose() {
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
