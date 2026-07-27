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
import io.github.simple_game.core.service.DragAndDropManager;
import io.github.simple_game.core.service.GameLoop;

public class ShopPanel extends Table {
    private final GameLoop gameLoop;
    private final DragAndDropManager dragManager;
    private Texture backgroundTexture;

    public ShopPanel(GameLoop gameLoop, GameRenderer renderer, DragAndDropManager dragManager) {
        this.gameLoop = gameLoop;
        this.dragManager = dragManager;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.12f, 0.12f, 0.12f, 1f));
        pixmap.fill();
        this.backgroundTexture = new Texture(pixmap);
        pixmap.dispose();
        this.setBackground(new TextureRegionDrawable(backgroundTexture));

        Table slot1 = createShopSlot(new Image(renderer.getArcherTowerTexture()), "100G", TowerType.ARCHER);
        Table slot2 = createShopSlot(new Image(renderer.getCannonTowerTexture()), "250G", TowerType.CANNON);
        Table slot3 = createShopSlot(new Image(renderer.getMagicTowerTexture()),  "200G", TowerType.MAGIC);

        this.bottom().center();
        this.add(slot1).expandX().fillY().padTop(10).padBottom(15);
        this.add(slot2).expandX().fillY().padTop(10).padBottom(15);
        this.add(slot3).expandX().fillY().padTop(10).padBottom(15);
    }

    private Table createShopSlot(Image towerImage, String priceText, final TowerType towerType) {
        Table slotTable = new Table();
        Label priceLabel = new Label(priceText, new Label.LabelStyle(new BitmapFont(), Color.GOLD));
        priceLabel.getStyle().font.getData().setScale(1.2f);

        slotTable.add(towerImage).size(64, 64).padBottom(5);
        slotTable.row();
        slotTable.add(priceLabel);

        DragListener listener = new DragListener() {
            private boolean isDraggingActive = false;

            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                if (dragManager == null) return;

                // Передаем точные системные пиксели клика в менеджер
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

        // Заставляем драг активироваться МГНОВЕННО (уменьшаем мертвую зону с 14px до 1px)
        listener.setTapSquareSize(1f);
        slotTable.addListener(listener);

        return slotTable;
    }

    @Override
    public float getPrefWidth() { return Gdx.graphics.getWidth(); }

    public void dispose() {
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
