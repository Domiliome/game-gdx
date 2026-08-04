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

public class ForgePanel extends Table {
    public ForgePanel(InventoryManager inv, DragAndDrop dad, Label.LabelStyle textStyle, TextButton.TextButtonStyle slotStyle, TextButton.TextButtonStyle fStyle, java.util.function.Consumer<String> onStatusMsg) {
        this.center();
        for (int i = 0; i < 3; i++) {
            if (i < inv.getForgeSlots().size) {
                final Item item = inv.getForgeSlots().get(i);
                TextButton b = new TextButton("[" + item.getName().substring(0, 2).toUpperCase() + "]", fStyle);
                b.addListener(new ClickListener() {
                    @Override public void clicked(InputEvent e, float x, float y) { inv.removeItemFromForge(item); onStatusMsg.accept("RESET"); }
                });
                this.add(b).size(60, 60).pad(2);
            } else {
                TextButton emptyForge = new TextButton("[+]", slotStyle);
                this.add(emptyForge).size(60, 60).pad(2);
                dad.addTarget(new DragAndDrop.Target(emptyForge) {
                    @Override public boolean drag(DragAndDrop.Source s, DragAndDrop.Payload p, float x, float y, int ptr) { return true; }
                    @Override public void drop(DragAndDrop.Source s, DragAndDrop.Payload p, float x, float y, int ptr) { inv.addItemToForge((Item) p.getObject()); onStatusMsg.accept("RESET"); }
                });
            }
        }
        this.add(new Label(">", textStyle)).pad(2);

        // ИСПРАВЛЕНО: Безопасное извлечение и форматирование превью создаваемого предмета
        Item craftResult = inv.getCraftResult();
        String previewText;

        if (craftResult != null) {
            previewText = "[" + craftResult.getName().substring(0, 2).toUpperCase() + "]";
        } else {
            previewText = "[?]";
        }

        TextButton previewSlot = new TextButton(previewText, slotStyle);

        if (craftResult != null) {
            previewSlot.getLabel().getStyle().fontColor = Color.GREEN;
        }

        this.add(previewSlot).size(60, 60).pad(2);
    }
}
