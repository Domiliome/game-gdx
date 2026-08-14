package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.service.InventoryManager;

public class EquipmentPanel extends Table {
    private static final float SLOT_SIZE = 58f;

    public EquipmentPanel(InventoryManager inv, DragAndDrop dad, TextButton.TextButtonStyle slotStyle,
                          TextButton.TextButtonStyle activeStyle, Drawable slotBackground,
                          Drawable activeBackground, Runnable onUpdate) {
        this.center();
        for (int i = 0; i < 3; i++) {
            if (i < inv.getEquippedSlots().size) {
                final Item item = inv.getEquippedSlots().get(i);
                Actor slot = ItemSlot.create(item, activeBackground, activeStyle, SLOT_SIZE);
                ItemSlot.addClick(slot, () -> {
                    inv.unequipItem(item);
                    onUpdate.run();
                });
                this.add(slot).size(SLOT_SIZE, SLOT_SIZE).pad(2);
            } else {
                TextButton empty = new TextButton("[EQ]", slotStyle);
                this.add(empty).size(SLOT_SIZE, SLOT_SIZE).pad(2);
                dad.addTarget(new DragAndDrop.Target(empty) {
                    @Override
                    public boolean drag(DragAndDrop.Source s, DragAndDrop.Payload p, float x, float y, int ptr) {
                        return true;
                    }

                    @Override
                    public void drop(DragAndDrop.Source s, DragAndDrop.Payload p, float x, float y, int ptr) {
                        inv.equipItem((Item) p.getObject());
                        onUpdate.run();
                    }
                });
            }
        }
    }
}
