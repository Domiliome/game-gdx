package io.github.simple_game.core.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen; // ИСПРАВЛЕНО: Импортируем базовый интерфейс Screen LibGDX
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import io.github.simple_game.core.Main;
import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.service.GameLoop;

public class InventoryScreen extends ScreenAdapter {
    private final Main game;
    private final Screen previousScreen;
    private final GameLoop gameLoop;
    private final Stage stage; private final Texture btnBg, slotBg, activeBg;
    private Label descLabel;

    public InventoryScreen(Main game, Screen previousScreen, GameLoop gameLoop) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.gameLoop = gameLoop;

        OrthographicCamera uiCamera = new OrthographicCamera();
        this.stage = new Stage(new ExtendViewport(480, 800, uiCamera));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.3f, 0.3f, 0.3f, 1f)); this.btnBg = new Texture(pixmap);
        pixmap.setColor(new Color(0.18f, 0.18f, 0.2f, 1f)); this.slotBg = new Texture(pixmap);
        pixmap.setColor(new Color(0.1f, 0.4f, 0.2f, 1f)); this.activeBg = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); stage.clear();
        var inv = this.gameLoop.getInventoryManager();
        DragAndDrop dad = new DragAndDrop();

        Label.LabelStyle textStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE); textStyle.font.getData().setScale(1.4f);
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle(); btnStyle.font = new BitmapFont(); btnStyle.font.getData().setScale(1.8f); btnStyle.fontColor = Color.WHITE; btnStyle.up = new TextureRegionDrawable(btnBg);
        TextButton.TextButtonStyle slotStyle = new TextButton.TextButtonStyle(); slotStyle.font = new BitmapFont(); slotStyle.font.getData().setScale(1.3f); slotStyle.fontColor = Color.GOLD; slotStyle.up = new TextureRegionDrawable(slotBg);
        TextButton.TextButtonStyle activeStyle = new TextButton.TextButtonStyle(); activeStyle.font = new BitmapFont(); activeStyle.font.getData().setScale(1.3f); activeStyle.fontColor = Color.GREEN; activeStyle.up = new TextureRegionDrawable(activeBg);

        Table mainTable = new Table(); mainTable.setFillParent(true); mainTable.top().pad(15);

        Label slotsTitle = new Label("--- ACTIVE SLOTS (DRAG HERE) ---", textStyle); slotsTitle.setColor(Color.GREEN);
        mainTable.add(slotsTitle).padBottom(5).row();

        Table activeSlotsTable = new Table();
        for (int i = 0; i < 3; i++) {
            if (i < inv.getEquippedSlots().size) {
                final Item item = inv.getEquippedSlots().get(i);
                TextButton eqBtn = new TextButton("[" + item.getName().substring(0,2).toUpperCase() + "]", activeStyle);
                eqBtn.addListener(new ClickListener() {
                    @Override public void clicked(InputEvent e, float x, float y) { inv.unequipItem(item); show(); }
                });
                activeSlotsTable.add(eqBtn).size(85, 85).pad(8);
            } else {
                TextButton emptyBtn = new TextButton("[EMPTY]", slotStyle);
                activeSlotsTable.add(emptyBtn).size(85, 85).pad(8);
                dad.addTarget(new DragAndDrop.Target(emptyBtn) {
                    @Override public boolean drag(DragAndDrop.Source s, DragAndDrop.Payload p, float x, float y, int ptr) { return true; }
                    @Override public void drop(DragAndDrop.Source s, DragAndDrop.Payload p, float x, float y, int ptr) {
                        inv.equipItem((Item) p.getObject()); show();
                    }
                });
            }
        }
        mainTable.add(activeSlotsTable).padBottom(10).row();

        Label packTitle = new Label("--- BACKPACK ITEMS ---", textStyle); packTitle.setColor(Color.GOLD);
        mainTable.add(packTitle).padBottom(5).row();

        Table gridTable = new Table(); gridTable.top().left();
        for (int i = 0; i < inv.getBackpack().size; i++) {
            final Item item = inv.getBackpack().get(i);
            TextButton slotBtn = new TextButton("[" + item.getName().substring(0,2).toUpperCase() + "]", slotStyle);

            slotBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    descLabel.setText(item.getName() + "\n" + item.getDescription());
                }
            });

            dad.addSource(new DragAndDrop.Source(slotBtn) {
                @Override public DragAndDrop.Payload dragStart(InputEvent e, float x, float y, int ptr) {
                    DragAndDrop.Payload p = new DragAndDrop.Payload(); p.setObject(item);
                    Label ghost = new Label(item.getName().substring(0,2).toUpperCase(), textStyle); ghost.setColor(Color.GREEN);
                    p.setDragActor(ghost); return p;
                }
            });
            gridTable.add(slotBtn).size(80, 80).pad(6);
            if ((i + 1) % 4 == 0) gridTable.row();
        }

        ScrollPane scrollPane = new ScrollPane(gridTable); scrollPane.setScrollingDisabled(true, false);
        mainTable.add(scrollPane).expand().fill().padBottom(10).row();

        descLabel = new Label("Select an item to see description\nor drag it to active slot", textStyle);
        descLabel.setColor(Color.LIGHT_GRAY); descLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        mainTable.add(descLabel).width(440).height(75).padBottom(10).row();

        TextButton backBtn = new TextButton(" RETURN ", btnStyle);
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                if (previousScreen != null) {
                    game.setScreen(previousScreen);
                }
            }
        });
        mainTable.add(backBtn).size(300, 55); stage.addActor(mainTable);
    }

    @Override public void render(float d) { Gdx.gl.glClearColor(0.1f, 0.1f, 0.12f, 1f); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(d); stage.draw(); }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void dispose() { stage.dispose(); btnBg.dispose(); slotBg.dispose(); activeBg.dispose(); }
}
