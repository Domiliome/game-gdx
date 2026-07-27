package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.service.GameLoop;

public class DebugGridRenderer {

    public DebugGridRenderer(GameLoop gameLoop) {
    }

        public void render(ShapeRenderer shapeRenderer, float worldHeight) { // Добавляем worldHeight
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(new Color(1, 1, 1, 0.1f));

        // Вертикальные линии рисуем до самого верха новой границы мира
        for (int x = 0; x <= 480; x += 64) shapeRenderer.line(x, 0, x, worldHeight);
        // Горизонтальные линии продолжаем генерировать выше 800
        for (int y = 0; y <= worldHeight; y += 64) shapeRenderer.line(0, y, 480, y);

        // ... остальной код отрисовки путей и радиусов башен остается прежним
        shapeRenderer.end();
    }

}
