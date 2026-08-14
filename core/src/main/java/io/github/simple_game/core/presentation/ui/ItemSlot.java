package io.github.simple_game.core.presentation.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

import io.github.simple_game.core.model.entity.items.Item;

/**
 * Ячейка предмета: иконка, если PNG найден, иначе двухбуквенная подпись.
 */
public final class ItemSlot {
    private ItemSlot() {}

    public static Actor create(Item item, Drawable slotBackground, TextButton.TextButtonStyle fallbackStyle, float size) {
        Image icon = ItemIcons.createImage(item, size * 0.78f);
        if (icon != null) {
            icon.setScaling(Scaling.fit);
            Table slot = new Table();
            slot.setBackground(slotBackground);
            slot.add(icon).size(size * 0.78f);
            return slot;
        }
        return new TextButton("[" + abbrev(item) + "]", fallbackStyle);
    }

    public static Actor createDragGhost(Item item, Label.LabelStyle textStyle) {
        Image icon = ItemIcons.createImage(item, 48);
        if (icon != null) {
            icon.setScaling(Scaling.fit);
            icon.setColor(1f, 1f, 1f, 0.9f);
            return icon;
        }
        Label ghost = new Label(abbrev(item), textStyle);
        ghost.setColor(Color.GREEN);
        return ghost;
    }

    public static void addClick(Actor actor, Runnable action) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                action.run();
            }
        });
    }

    public static String abbrev(Item item) {
        String name = item.getName();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
}
