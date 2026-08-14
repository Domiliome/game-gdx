package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.service.InventoryManager;

public class BackpackGrid extends Table {
    private static final float SLOT_SIZE = 64f;
    private static final int COLS = 5;

    public BackpackGrid(InventoryManager inv, DragAndDrop dad, Label.LabelStyle textStyle,
                        TextButton.TextButtonStyle slotStyle, Drawable slotBackground,
                        java.util.function.Consumer<Item> onSelect) {
        this.top().left();
        for (int i = 0; i < inv.getBackpack().size; i++) {
            final Item item = inv.getBackpack().get(i);
            Actor slot = ItemSlot.create(item, slotBackground, slotStyle, SLOT_SIZE);
            ItemSlot.addClick(slot, () -> onSelect.accept(item));
            dad.addSource(new DragAndDrop.Source(slot) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent e, float x, float y, int ptr) {
                    DragAndDrop.Payload p = new DragAndDrop.Payload();
                    p.setObject(item);
                    p.setDragActor(ItemSlot.createDragGhost(item, textStyle));
                    return p;
                }
            });
            this.add(slot).size(SLOT_SIZE, SLOT_SIZE).pad(3);
            if ((i + 1) % COLS == 0) this.row();
        }
    }
}
