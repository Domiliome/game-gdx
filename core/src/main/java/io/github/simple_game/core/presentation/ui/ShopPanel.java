package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import io.github.simple_game.core.model.entity.tower.TowerType;
import io.github.simple_game.core.presentation.GameViewport;
import io.github.simple_game.core.presentation.view.GameRenderer;
import io.github.simple_game.core.service.DragAndDropManager;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.ShopService;

public class ShopPanel extends Table {
    private static final int TOTAL_COLUMNS = ShopService.SLOT_COUNT + 1;

    private final GameLoop gameLoop;
    private final GameRenderer renderer;
    private final DragAndDropManager dragManager;
    private final Texture backgroundTexture;
    private final Texture refreshButtonTexture;
    private final Array<Label> priceLabels = new Array<>();
    private final Array<TowerType> shopTypes = new Array<>();
    private final Table slotsRow = new Table();
    private final Label.LabelStyle priceStyle;
    private final TextButton refreshButton;
    private final float slotSize;

    public ShopPanel(GameLoop gameLoop, GameRenderer renderer, DragAndDropManager dragManager) {
        this.gameLoop = gameLoop;
        this.renderer = renderer;
        this.dragManager = dragManager;
        this.slotSize = computeSlotSize(TOTAL_COLUMNS);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.12f, 0.12f, 0.12f, 1f));
        pixmap.fill();
        this.backgroundTexture = new Texture(pixmap);
        pixmap.setColor(new Color(0.2f, 0.45f, 0.2f, 0.9f));
        pixmap.fill();
        this.refreshButtonTexture = new Texture(pixmap);
        pixmap.dispose();
        this.setBackground(new TextureRegionDrawable(backgroundTexture));

        this.priceStyle = new Label.LabelStyle(new BitmapFont(), Color.GOLD);
        priceStyle.font.getData().setScale(MathUtils.clamp(slotSize / 48f, 1.0f, 1.35f));
        this.bottom().center().padBottom(8).padTop(6);

        TextButton.TextButtonStyle refreshStyle = new TextButton.TextButtonStyle();
        refreshStyle.font = new BitmapFont();
        refreshStyle.font.getData().setScale(MathUtils.clamp(slotSize / 48f, 0.9f, 1.2f));
        refreshStyle.fontColor = Color.WHITE;
        refreshStyle.up = new TextureRegionDrawable(refreshButtonTexture);

        refreshButton = new TextButton("↻ " + ShopService.REFRESH_COST + "G", refreshStyle);
        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                tryRefreshShop();
            }
        });

        this.add(slotsRow).expandX().fillX();
        this.add(refreshButton).width(slotSize + 16).height(slotSize + 24).padLeft(6);
        rebuildSlots();
    }

    private void tryRefreshShop() {
        if (gameLoop.getCurrencyManager().spendGold(ShopService.REFRESH_COST)) {
            gameLoop.getShopService().refreshShop();
            rebuildSlots();
        }
    }

    private void rebuildSlots() {
        slotsRow.clearChildren();
        shopTypes.clear();
        priceLabels.clear();

        for (TowerType type : gameLoop.getShopService().getShopSlots()) {
            Label priceLabel = new Label(type.getCost() + "G", priceStyle);
            Table slot = createShopSlot(new Image(renderer.getTowerTexture(type)), priceLabel, type);
            shopTypes.add(type);
            priceLabels.add(priceLabel);
            slotsRow.add(slot).expandX().fillY().uniformX();
        }
    }

    private static float computeSlotSize(int columnCount) {
        if (columnCount <= 0) return 72f;
        float available = GameViewport.WIDTH - 8f;
        return Math.min(72f, (available / columnCount) - 2f);
    }

    private Table createShopSlot(Image towerImage, Label priceLabel, final TowerType towerType) {
        Table slotTable = new Table();

        slotTable.add(towerImage).size(slotSize, slotSize).padBottom(2);
        slotTable.row();
        slotTable.add(priceLabel);

        DragListener listener = new DragListener() {
            private boolean isDraggingActive = false;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                if (dragManager == null) return;
                dragManager.startDrag(towerType, com.badlogic.gdx.Gdx.input.getX(), com.badlogic.gdx.Gdx.input.getY());
                isDraggingActive = true;
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (!isDraggingActive || dragManager == null) return;
                dragManager.updatePosition(com.badlogic.gdx.Gdx.input.getX(), com.badlogic.gdx.Gdx.input.getY());
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (!isDraggingActive || dragManager == null) return;
                if (dragManager.stopDragAndPlace()) {
                    gameLoop.getShopService().refreshShop();
                    rebuildSlots();
                }
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
        for (int i = 0; i < shopTypes.size; i++) {
            TowerType type = shopTypes.get(i);
            priceLabels.get(i).setColor(currentGold >= type.getCost() ? Color.GOLD : Color.RED);
        }
        refreshButton.setColor(currentGold >= ShopService.REFRESH_COST ? Color.WHITE : Color.GRAY);
    }

    public void dispose() {
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (refreshButtonTexture != null) refreshButtonTexture.dispose();
    }
}
