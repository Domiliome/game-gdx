package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.service.InventoryManager;

public class BackpackGrid extends Table {
    public BackpackGrid(InventoryManager inv, DragAndDrop dad, Label.LabelStyle textStyle, TextButton.TextButtonStyle slotStyle, java.util.function.Consumer<Item> onSelect) {
        this.top().left();
        for (int i = 0; i < inv.getBackpack().size; i++) {
            final Item item = inv.getBackpack().get(i);
            TextButton slotBtn = new TextButton("[" + item.getName().substring(0, 2).toUpperCase() + "]", slotStyle);
            slotBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) { onSelect.accept(item); }
            });
            dad.addSource(new DragAndDrop.Source(slotBtn) {
                @Override public DragAndDrop.Payload dragStart(InputEvent e, float x, float y, int ptr) {
                    DragAndDrop.Payload p = new DragAndDrop.Payload(); p.setObject(item);
                    Label ghost = new Label(item.getName().substring(0, 2).toUpperCase(), textStyle); ghost.setColor(Color.GREEN);
                    p.setDragActor(ghost); return p;
                }
            });
            this.add(slotBtn).size(68, 68).pad(4);
            if ((i + 1) % 5 == 0) this.row();
        }
    }
}
