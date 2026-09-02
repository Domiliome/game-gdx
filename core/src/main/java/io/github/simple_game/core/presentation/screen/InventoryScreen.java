package io.github.simple_game.core.presentation.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import io.github.simple_game.core.presentation.GameViewport;
import io.github.simple_game.core.presentation.ui.BackpackGrid;
import io.github.simple_game.core.presentation.ui.EquipmentPanel;
import io.github.simple_game.core.presentation.ui.ForgePanel;
import io.github.simple_game.core.service.InventoryManager;

public class InventoryScreen extends ScreenAdapter {
    private final Main game;
    private final Screen previousScreen;
    private final InventoryManager inventory;
    private final Stage stage;
    private final Texture btnBg, slotBg, activeBg, forgeBg;
    private final TextureRegionDrawable slotDrawable, activeDrawable, forgeDrawable;
    private Label descLabel;

    public InventoryScreen(Main game, Screen previousScreen, InventoryManager inventory) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.inventory = inventory;
        this.stage = new Stage(new ExtendViewport(GameViewport.WIDTH, GameViewport.HEIGHT, new OrthographicCamera()));
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(new Color(0.3f, 0.3f, 0.3f, 1f));
        this.btnBg = new Texture(p);
        p.setColor(new Color(0.15f, 0.15f, 0.16f, 1f));
        this.slotBg = new Texture(p);
        p.setColor(new Color(0.1f, 0.35f, 0.15f, 1f));
        this.activeBg = new Texture(p);
        p.setColor(new Color(0.45f, 0.2f, 0.1f, 1f));
        this.forgeBg = new Texture(p);
        p.dispose();

        this.slotDrawable = new TextureRegionDrawable(slotBg);
        this.activeDrawable = new TextureRegionDrawable(activeBg);
        this.forgeDrawable = new TextureRegionDrawable(forgeBg);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.clear();
        var inv = this.inventory;
        DragAndDrop dad = new DragAndDrop();

        Label.LabelStyle tStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        tStyle.font.getData().setScale(1.1f);
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = new BitmapFont();
        btnStyle.font.getData().setScale(1.35f);
        btnStyle.fontColor = Color.WHITE;
        btnStyle.up = new TextureRegionDrawable(btnBg);
        TextButton.TextButtonStyle slotStyle = new TextButton.TextButtonStyle();
        slotStyle.font = new BitmapFont();
        slotStyle.font.getData().setScale(0.95f);
        slotStyle.fontColor = Color.GOLD;
        slotStyle.up = slotDrawable;
        TextButton.TextButtonStyle actStyle = new TextButton.TextButtonStyle();
        actStyle.font = new BitmapFont();
        actStyle.font.getData().setScale(0.95f);
        actStyle.fontColor = Color.GREEN;
        actStyle.up = activeDrawable;
        TextButton.TextButtonStyle fStyle = new TextButton.TextButtonStyle();
        fStyle.font = new BitmapFont();
        fStyle.font.getData().setScale(0.95f);
        fStyle.fontColor = Color.ORANGE;
        fStyle.up = forgeDrawable;

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().pad(8);

        Table topPanel = new Table();
        topPanel.add(new EquipmentPanel(inv, dad, slotStyle, actStyle, slotDrawable, activeDrawable, this::show)).padRight(10);
        topPanel.add(new ForgePanel(inv, dad, tStyle, slotStyle, fStyle, slotDrawable, forgeDrawable, msg -> show())).row();
        mainTable.add(topPanel).padBottom(5).row();

        boolean canForge = (inv.getCraftResult() != null);
        TextButton forgeBtn = new TextButton("PRESS TO FORGE", btnStyle);
        forgeBtn.getLabel().getStyle().fontColor = canForge ? Color.ORANGE : Color.GRAY;
        forgeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (inv.getCraftResult() != null) {
                    boolean s = inv.forge();
                    descLabel.setText(s ? "SUCCESS!" : "FAILED");
                    descLabel.setColor(s ? Color.GREEN : Color.RED);
                    show();
                }
            }
        });
        mainTable.add(forgeBtn).size(220, 38).padBottom(6).row();

        mainTable.add(new Label("- BACKPACK -", tStyle)).padBottom(2).row();
        BackpackGrid backpackGrid = new BackpackGrid(inv, dad, tStyle, slotStyle, slotDrawable, item -> {
            descLabel.setText(item.getName() + ": " + item.getDescription());
            descLabel.setColor(Color.LIGHT_GRAY);
        });
        ScrollPane scrollPane = new ScrollPane(backpackGrid);
        scrollPane.setScrollingDisabled(true, false);
        mainTable.add(scrollPane).expand().fill().padBottom(5).row();

        descLabel = new Label("Drag items to active [EQ] slots or orange [+] forge slots", tStyle);
        descLabel.setColor(Color.LIGHT_GRAY);
        descLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        mainTable.add(descLabel).width(440).height(36).padBottom(4).row();
        TextButton backBtn = new TextButton(" RETURN ", btnStyle);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (previousScreen != null) game.setScreen(previousScreen);
            }
        });
        mainTable.add(backBtn).size(220, 40);
        stage.addActor(mainTable);
    }

    @Override
    public void render(float d) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(d);
        stage.draw();
    }

    @Override
    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        btnBg.dispose();
        slotBg.dispose();
        activeBg.dispose();
        forgeBg.dispose();
    }
}
