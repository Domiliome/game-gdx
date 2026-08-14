package io.github.simple_game.core.presentation.view.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

import io.github.simple_game.core.model.entity.map.GameGrid;
import io.github.simple_game.core.model.entity.tower.Tower;
import io.github.simple_game.core.model.entity.tower.TowerType;
import io.github.simple_game.core.service.GameLoop;

public class WorldSpriteRenderer {
    private final GameLoop gameLoop;
    private final Texture tileLight, tileDark;
    private final TextureRegion roadStraight, roadTurn;
    private final ObjectMap<TowerType, Texture> towerTextures = new ObjectMap<>();

    private static final int TOWER_VISUAL_SIZE = GameGrid.CELL_SIZE * 2;

    public WorldSpriteRenderer(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        this.tileLight = new Texture(Gdx.files.internal("tiles/light.png"));
        this.tileDark = new Texture(Gdx.files.internal("tiles/dark.png"));

        Texture straightTex = new Texture(Gdx.files.internal("tiles/road_straight.png"));
        Texture turnTex = new Texture(Gdx.files.internal("tiles/road_turn.png"));
        this.roadStraight = new TextureRegion(straightTex);
        this.roadTurn = new TextureRegion(turnTex);

        tileLight.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        tileDark.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        straightTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        turnTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        for (TowerType type : TowerType.values()) {
            Texture texture = new Texture(Gdx.files.internal(type.getIdleTexturePath()));
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            towerTextures.put(type, texture);
        }
    }

    public void renderBackground(SpriteBatch batch, float worldHeight) {
        var roadPath = gameLoop.getRoadPath();
        int cols = GameGrid.columnCount();
        int rows = (int) (worldHeight / GameGrid.CELL_SIZE) + 1;
        boolean[][] isRoad = new boolean[cols][rows];

        GameGrid.fillRoadMask(isRoad, roadPath);

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                float drawX = x * GameGrid.CELL_SIZE;
                float drawY = y * GameGrid.CELL_SIZE;

                if (!isRoad[x][y]) {
                    Texture currentTile = ((x + y) % 2 == 0) ? tileLight : tileDark;
                    batch.draw(currentTile, drawX, drawY, GameGrid.CELL_SIZE, GameGrid.CELL_SIZE);
                    continue;
                }

                boolean left  = x > 0 && isRoad[x - 1][y];
                boolean right = x < cols - 1 && isRoad[x + 1][y];
                boolean down  = y > 0 && isRoad[x][y - 1];
                boolean up    = y < rows - 1 && isRoad[x][y + 1];

                RoadTile tile = pickRoadTile(left, right, down, up);
                float origin = GameGrid.CELL_SIZE / 2f;
                batch.draw(tile.region(), drawX, drawY, origin, origin,
                        GameGrid.CELL_SIZE, GameGrid.CELL_SIZE, 1f, 1f, tile.rotation());
            }
        }
    }

    private record RoadTile(TextureRegion region, float rotation) {}

    private RoadTile pickRoadTile(boolean left, boolean right, boolean down, boolean up) {
        int horizontal = (left ? 1 : 0) + (right ? 1 : 0);
        int vertical = (down ? 1 : 0) + (up ? 1 : 0);

        if (horizontal == 2) {
            return new RoadTile(roadStraight, 90f);
        }
        if (vertical == 2) {
            return new RoadTile(roadStraight, 0f);
        }

        if (right && up)   return new RoadTile(roadTurn, 0f);
        if (right && down) return new RoadTile(roadTurn, 270f);
        if (left && up)    return new RoadTile(roadTurn, 90f);
        if (left && down)  return new RoadTile(roadTurn, 180f);

        if (up || down)    return new RoadTile(roadStraight, 0f);
        if (left || right) return new RoadTile(roadStraight, 90f);

        return new RoadTile(roadStraight, 0f);
    }

    public void renderTowers(SpriteBatch batch) {
        for (Tower tower : gameLoop.getTowers()) {
            float drawX = tower.getPosition().x - (TOWER_VISUAL_SIZE / 2f);
            float drawY = tower.getPosition().y - (TOWER_VISUAL_SIZE / 2f);

            if (tower.isInitializing()) {
                TextureRegion currentFrame = tower.getCurrentInitFrame();
                if (currentFrame != null) {
                    batch.draw(currentFrame, drawX, drawY, TOWER_VISUAL_SIZE, TOWER_VISUAL_SIZE);
                    continue;
                }
            }
            batch.draw(getTexture(tower.getType()), drawX, drawY, TOWER_VISUAL_SIZE, TOWER_VISUAL_SIZE);
        }
    }

    public Texture getTexture(TowerType type) {
        return towerTextures.get(type);
    }

    public void dispose() {
        tileLight.dispose();
        tileDark.dispose();
        roadStraight.getTexture().dispose();
        roadTurn.getTexture().dispose();
        for (Texture texture : towerTextures.values()) {
            texture.dispose();
        }
        towerTextures.clear();
    }
}
