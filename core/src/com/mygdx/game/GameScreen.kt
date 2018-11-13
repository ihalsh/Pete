package com.mygdx.game

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.Entities.Pete
import com.mygdx.game.Utils.Assets.assetManager
import com.mygdx.game.Utils.Constants.Companion.MAP_FILE_NAME
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import ktx.app.KtxScreen

class GameScreen() : KtxScreen {

    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
    private val tiledMap: TiledMap = assetManager.get(MAP_FILE_NAME)
    private val orthogonalTiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, batch)

    private val pete: Pete = Pete()

    override fun show() {
        viewport.apply()
        camera.update()
        orthogonalTiledMapRenderer.setView(camera)
    }

    override fun render(delta: Float) {
//        clearScreen(BLACK.r, BLACK.g, BLACK.b)
        update(delta)
        draw()
        drawDebug()
    }

    private fun update(delta: Float) {

        pete.update()

    }

    private fun draw() {
        batch.projectionMatrix = camera.projection
        batch.transformMatrix = camera.view
        orthogonalTiledMapRenderer.render()
    }

    private fun drawDebug() {
        shapeRenderer.projectionMatrix = camera.projection
        shapeRenderer.transformMatrix = camera.view
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        pete.drawDebug(shapeRenderer)
        shapeRenderer.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun dispose() {
        shapeRenderer.dispose()
        orthogonalTiledMapRenderer.dispose()
        batch.dispose()
    }
}