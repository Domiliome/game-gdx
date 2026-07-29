package io.github.simple_game.core.presentation.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.simple_game.core.model.entity.tower.TowerType;
import io.github.simple_game.core.presentation.view.renderer.DebugGridRenderer;
import io.github.simple_game.core.presentation.view.renderer.EntityRenderer;
import io.github.simple_game.core.presentation.view.renderer.HealthBarRenderer;
import io.github.simple_game.core.presentation.view.renderer.WorldSpriteRenderer;
import io.github.simple_game.core.service.GameLoop;
import io.github.simple_game.core.service.InteractionService;

public class GameRenderer {
    private final OrthographicCamera camera;
    private final InteractionService interactionService;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    private final WorldSpriteRenderer worldSpriteRenderer;
    private final DebugGridRenderer debugGridRenderer;
    private final EntityRenderer entityRenderer;
    private final HealthBarRenderer healthBarRenderer;

    public GameRenderer(GameLoop gameLoop, OrthographicCamera camera, InteractionService interactionService) {
        this.camera = camera;
        this.interactionService = interactionService;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        this.worldSpriteRenderer = new WorldSpriteRenderer(gameLoop);
        this.debugGridRenderer = new DebugGridRenderer(gameLoop);
        this.entityRenderer = new EntityRenderer(gameLoop);
        this.healthBarRenderer = new HealthBarRenderer(gameLoop);
    }

            public void render() {
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);


        float currentWorldHeight = camera.viewportHeight;

        boolean isDraggingActive = interactionService != null &&
                                   interactionService.getDragAndDropManager().isDragging();


        batch.begin();
        worldSpriteRenderer.render(batch, currentWorldHeight);
        batch.end();

        debugGridRenderer.render(shapeRenderer, currentWorldHeight, isDraggingActive);
        entityRenderer.render(shapeRenderer);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        healthBarRenderer.render(shapeRenderer, batch);


        if (isDraggingActive) {
            interactionService.getDragAndDropManager().drawPreview(shapeRenderer);
            renderGhostPhantom();
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }




    private void renderGhostPhantom() {
        TowerType dragType = interactionService.getDragAndDropManager().getDraggingType();
        if (dragType != null) {
            batch.begin();
            batch.setColor(1, 1, 1, 0.6f);
            batch.draw(worldSpriteRenderer.getTexture(dragType),
                    interactionService.getDragAndDropManager().getCurrentX() - 32f,
                    interactionService.getDragAndDropManager().getCurrentY() - 32f, 64, 64);
            batch.setColor(1, 1, 1, 1f);
            batch.end();
        }
    }

    public Texture getArcherTowerTexture() { return worldSpriteRenderer.getTexture(TowerType.ARCHER); }
    public Texture getCannonTowerTexture() { return worldSpriteRenderer.getTexture(TowerType.CANNON); }
    public Texture getMagicTowerTexture() { return worldSpriteRenderer.getTexture(TowerType.MAGIC); }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        worldSpriteRenderer.dispose();
    }
}
