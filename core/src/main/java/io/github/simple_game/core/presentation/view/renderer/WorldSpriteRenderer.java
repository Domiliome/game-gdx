package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import io.github.simple_game.core.model.entity.tower.ArcherTower;
import io.github.simple_game.core.model.entity.tower.CannonTower;
import io.github.simple_game.core.model.entity.tower.MagicTower;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.entity.tower.TowerType;
import io.github.simple_game.core.service.GameLoop;

public class WorldSpriteRenderer {
    private final GameLoop gameLoop;
    private final Texture tileLight, tileDark;
    private final TextureRegion roadStraight, roadTurn;
    private final Texture archerTexture, cannonTexture, magicTexture;

    private static final int LOGIC_CELL_SIZE = 32;
    private static final int TOWER_VISUAL_SIZE = 64;
    private final Rectangle cellBounds = new Rectangle();

    public WorldSpriteRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        this.tileLight = new Texture(Gdx.files.internal("tile_light.png"));
        this.tileDark = new Texture(Gdx.files.internal("tile_dark.png"));

        Texture straightTex = new Texture(Gdx.files.internal("tile_road_straight.png"));
        Texture turnTex = new Texture(Gdx.files.internal("tile_road_turn.png"));
        this.roadStraight = new TextureRegion(straightTex);
        this.roadTurn = new TextureRegion(turnTex);

        this.archerTexture = new Texture(Gdx.files.internal("tower_archer.png"));
        this.cannonTexture = new Texture(Gdx.files.internal("tower_cannon.png"));
        this.magicTexture = new Texture(Gdx.files.internal("tower_magic.png"));

        tileLight.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        tileDark.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        straightTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        turnTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        archerTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        cannonTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        magicTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void renderBackground(SpriteBatch batch, float worldHeight) {
        var roadPath = gameLoop.getRoadPath();
        int cols = 480 / LOGIC_CELL_SIZE;
        int rows = (int) (worldHeight / LOGIC_CELL_SIZE) + 1;
        boolean[][] isRoad = new boolean[cols][rows];

        // Пасс 1: Картируем всю карту в логическую boolean-матрицу дороги
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                cellBounds.set(x * LOGIC_CELL_SIZE, y * LOGIC_CELL_SIZE, LOGIC_CELL_SIZE, LOGIC_CELL_SIZE);
                for (int i = 0; i < roadPath.getPointCount() - 1; i++) {
                    if (Intersector.intersectSegmentRectangle(roadPath.getPoint(i), roadPath.getPoint(i + 1), cellBounds)) {
                        isRoad[x][y] = true;
                        break;
                    }
                }
            }
        }

        // Пасс 2: Рендерим шахматный пол или авто-тайловую дорогу с вращением
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                float drawX = x * LOGIC_CELL_SIZE;
                float drawY = y * LOGIC_CELL_SIZE;

                if (!isRoad[x][y]) {
                    Texture currentTile = ((x + y) % 2 == 0) ? tileLight : tileDark;
                    batch.draw(currentTile, drawX, drawY, LOGIC_CELL_SIZE, LOGIC_CELL_SIZE);
                    continue;
                }

                // Сканируем 4 соседние ячейки вокруг текущего участка дороги
                boolean left  = x > 0 && isRoad[x - 1][y];
                boolean right = x < cols - 1 && isRoad[x + 1][y];
                boolean down  = y > 0 && isRoad[x][y - 1];
                boolean up    = y < rows - 1 && isRoad[x][y + 1];

                TextureRegion tileToDraw = roadStraight;
                float rotation = 0f;

                if (up && down) {
                    tileToDraw = roadStraight; rotation = 0f; // Теперь прямая текстура по умолчанию вертикальная
                } else if (left && right) {
                    tileToDraw = roadStraight; rotation = 90f; // Горизонтальные участки разворачиваем на 90 градусов
                }
                // Переворачиваем угловые коннекторы:
                else if (right && up) { tileToDraw = roadTurn; rotation = 0f; }
                else if (right && down) { tileToDraw = roadTurn; rotation = 270f; }
                else if (left && up) { tileToDraw = roadTurn; rotation = 90f; }
                else if (left && down) { tileToDraw = roadTurn; rotation = 180f; }
                // Корректируем тупики и стартовые позиции:
                else if (up || down) { tileToDraw = roadStraight; rotation = 0f; }
                else if (left || right) { tileToDraw = roadStraight; rotation = 90f; }

                // Отрисовываем заново с чистыми углами
                batch.draw(tileToDraw, drawX, drawY, 16f, 16f, LOGIC_CELL_SIZE, LOGIC_CELL_SIZE, 1f, 1f, rotation);

            }
        }
    }

    public void renderTowers(SpriteBatch batch) {
        for (Tower tower : gameLoop.getTowers()) {
            float drawX = tower.getPosition().x - (TOWER_VISUAL_SIZE / 2f);
            float drawY = tower.getPosition().y - (TOWER_VISUAL_SIZE / 2f);

            if (tower.getType() == TowerType.ARCHER && tower instanceof ArcherTower && ((ArcherTower) tower).isInitializing()) {
                TextureRegion currentFrame = ((ArcherTower) tower).getCurrentInitFrame();
                if (currentFrame != null) { batch.draw(currentFrame, drawX, drawY, TOWER_VISUAL_SIZE, TOWER_VISUAL_SIZE); continue; }
            }
            if (tower.getType() == TowerType.MAGIC && tower instanceof MagicTower && ((MagicTower) tower).isInitializing()) {
                TextureRegion currentFrame = ((MagicTower) tower).getCurrentInitFrame();
                if (currentFrame != null) { batch.draw(currentFrame, drawX, drawY, TOWER_VISUAL_SIZE, TOWER_VISUAL_SIZE); continue; }
            }
            // ИСПРАВЛЕНО: Убрана опечатка, теперь пушка проверяется корректно через переменную цикла "tower"
            if (tower.getType() == TowerType.CANNON && tower instanceof CannonTower && ((CannonTower) tower).isInitializing()) {
                TextureRegion currentFrame = ((CannonTower) tower).getCurrentInitFrame();
                if (currentFrame != null) { batch.draw(currentFrame, drawX, drawY, TOWER_VISUAL_SIZE, TOWER_VISUAL_SIZE); continue; }
            }
            batch.draw(getTexture(tower.getType()), drawX, drawY, TOWER_VISUAL_SIZE, TOWER_VISUAL_SIZE);
        }
    }

    public Texture getTexture(TowerType type) {
        return switch (type) { case ARCHER -> archerTexture; case CANNON -> cannonTexture; case MAGIC -> magicTexture; };
    }

    public void dispose() {
        tileLight.dispose(); tileDark.dispose();
        roadStraight.getTexture().dispose(); roadTurn.getTexture().dispose();
        archerTexture.dispose(); cannonTexture.dispose(); magicTexture.dispose();
    }
}
