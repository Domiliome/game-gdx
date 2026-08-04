package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import io.github.simple_game.core.model.entity.items.Item;
import io.github.simple_game.core.service.InventoryManager;

public class EquipmentPanel extends Table {
    public EquipmentPanel(InventoryManager inv, DragAndDrop dad, TextButton.TextButtonStyle slotStyle, TextButton.TextButtonStyle activeStyle, Runnable onUpdate) {
        this.center();
        for (int i = 0; i < 3; i++) {
            if (i < inv.getEquippedSlots().size) {
                final Item item = inv.getEquippedSlots().get(i);
                TextButton b = new TextButton("[" + item.getName().substring(0, 2).toUpperCase() + "]", activeStyle);
                b.addListener(new ClickListener() {
                    @Override public void clicked(InputEvent e, float x, float y) { inv.unequipItem(item); onUpdate.run(); }
                });
                this.add(b).size(60, 60).pad(3);
            } else {
                TextButton empty = new TextButton("[EQ]", slotStyle);
                this.add(empty).size(60, 60).pad(3);
                dad.addTarget(new DragAndDrop.Target(empty) {
                    @Override public boolean drag(DragAndDrop.Source s, DragAndDrop.Payload p, float x, float y, int ptr) { return true; }
                    @Override public void drop(DragAndDrop.Source s, DragAndDrop.Payload p, float x, float y, int ptr) { inv.equipItem((Item) p.getObject()); onUpdate.run(); }
                });
            }
        }
    }
}
