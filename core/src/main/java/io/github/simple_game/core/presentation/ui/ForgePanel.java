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

public class ForgePanel extends Table {
    private static final float SLOT_SIZE = 58f;

    public ForgePanel(InventoryManager inv, DragAndDrop dad, Label.LabelStyle textStyle,
                      TextButton.TextButtonStyle slotStyle, TextButton.TextButtonStyle fStyle,
                      Drawable slotBackground, Drawable forgeBackground,
                      java.util.function.Consumer<String> onStatusMsg) {
        this.center();
        for (int i = 0; i < 3; i++) {
            if (i < inv.getForgeSlots().size) {
                final Item item = inv.getForgeSlots().get(i);
                Actor slot = ItemSlot.create(item, forgeBackground, fStyle, SLOT_SIZE);
                ItemSlot.addClick(slot, () -> {
                    inv.removeItemFromForge(item);
                    onStatusMsg.accept("RESET");
                });
                this.add(slot).size(SLOT_SIZE, SLOT_SIZE).pad(2);
            } else {
                TextButton emptyForge = new TextButton("[+]", slotStyle);
                this.add(emptyForge).size(SLOT_SIZE, SLOT_SIZE).pad(2);
                dad.addTarget(new DragAndDrop.Target(emptyForge) {
                    @Override
                    public boolean drag(DragAndDrop.Source s, DragAndDrop.Payload p, float x, float y, int ptr) {
                        return true;
                    }

                    @Override
                    public void drop(DragAndDrop.Source s, DragAndDrop.Payload p, float x, float y, int ptr) {
                        inv.addItemToForge((Item) p.getObject());
                        onStatusMsg.accept("RESET");
                    }
                });
            }
        }
        this.add(new Label(">", textStyle)).pad(2);

        Item craftResult = inv.getCraftResult();
        if (craftResult != null) {
            Actor preview = ItemSlot.create(craftResult, slotBackground, slotStyle, SLOT_SIZE);
            this.add(preview).size(SLOT_SIZE, SLOT_SIZE).pad(2);
        } else {
            this.add(new TextButton("[?]", slotStyle)).size(SLOT_SIZE, SLOT_SIZE).pad(2);
        }
    }
}
