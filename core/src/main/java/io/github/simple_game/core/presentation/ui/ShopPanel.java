package io.github.simple_game.core.presentation.ui;

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
import io.github.simple_game.core.service.CurrencyManager;
import io.github.simple_game.core.service.DragAndDropManager;
import io.github.simple_game.core.service.GameLoop;

public class ShopPanel extends Table {
    private final GameLoop gameLoop;
    private final DragAndDropManager dragManager;
    private final Texture backgroundTexture; // Исправлено: добавили final

    // Храним ссылки на ценники, чтобы перекрашивать их в реальном времени
    private final Label priceLabel1;
    private final Label priceLabel2;
    private final Label priceLabel3;

    public ShopPanel(GameLoop gameLoop, GameRenderer renderer, DragAndDropManager dragManager) {
        this.gameLoop = gameLoop;
        this.dragManager = dragManager;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.12f, 0.12f, 0.12f, 1f));
        pixmap.fill();
        this.backgroundTexture = new Texture(pixmap);
        pixmap.dispose();
        this.setBackground(new TextureRegionDrawable(backgroundTexture));

        priceLabel1 = new Label("100G", new Label.LabelStyle(new BitmapFont(), Color.GOLD));
        priceLabel2 = new Label("250G", new Label.LabelStyle(new BitmapFont(), Color.GOLD));
        priceLabel3 = new Label("200G", new Label.LabelStyle(new BitmapFont(), Color.GOLD));

        Table slot1 = createShopSlot(new Image(renderer.getArcherTowerTexture()), priceLabel1, TowerType.ARCHER);
        Table slot2 = createShopSlot(new Image(renderer.getCannonTowerTexture()), priceLabel2, TowerType.CANNON);
        Table slot3 = createShopSlot(new Image(renderer.getMagicTowerTexture()),  priceLabel3, TowerType.MAGIC);

        this.bottom().center();
        this.add(slot1).expandX().fillY().padTop(10).padBottom(15);
        this.add(slot2).expandX().fillY().padTop(10).padBottom(15);
        this.add(slot3).expandX().fillY().padTop(10).padBottom(15);
    }

    private Table createShopSlot(Image towerImage, Label priceLabel, final TowerType towerType) {
        Table slotTable = new Table();
        priceLabel.getStyle().font.getData().setScale(1.2f);

        slotTable.add(towerImage).size(64, 64).padBottom(5);
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

    /**
     * Исправлено: Методact() вызывается сценой Scene2D автоматически каждый кадр.
     * Здесь мы ОПРАШИВАЕМ gameLoop, убирая предупреждение и добавляя динамику цен!
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        CurrencyManager economy = gameLoop.getCurrencyManager();
        int currentGold = economy.getGold();

        // Перекрашиваем цену в КРАСНЫЙ, если не хватает денег, и в ЗОЛОТОЙ, если хватает
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
