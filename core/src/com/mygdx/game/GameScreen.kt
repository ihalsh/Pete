package com.mygdx.game

import com.badlogic.gdx.graphics.Color.DARK_GRAY
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.Entities.Acorn
import com.mygdx.game.Entities.Pete
import com.mygdx.game.Utils.Assets
import com.mygdx.game.Utils.Assets.assetManager
import com.mygdx.game.Utils.Constants.Companion.ACORN_FILE_NAME
import com.mygdx.game.Utils.Constants.Companion.ACORN_LAYER
import com.mygdx.game.Utils.Constants.Companion.CELL_SIZE
import com.mygdx.game.Utils.Constants.Companion.PETE_WIDTH
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import com.mygdx.game.Utils.Constants.JumpState.GROUNDED
import com.mygdx.game.Utils.Constants.JumpState.JUMPING
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.graphics.use
import ktx.log.info


class GameScreen(private val pete: Pete = Pete()) : KtxScreen {

    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
    private val tiledMap = Assets.tiledMap
    private val orthogonalTiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, batch)
    private val acorns = Array<Acorn>()
    private var score = 0

    override fun show() {
        viewport.apply()
        camera.update()
        orthogonalTiledMapRenderer.setView(camera)
        populateAcorns()
    }

    private fun update(delta: Float) {

        pete.update(delta)
        pete.stopPeteLeavingTheScreen()
        handlePeteCollision()
        handlePeteCollisionWithAcorn()
    }

    override fun render(delta: Float) {
        clearScreen(DARK_GRAY.r, DARK_GRAY.g, DARK_GRAY.b)
        update(delta)
        draw()
        drawDebug()
    }

    private fun handlePeteCollisionWithAcorn() {
        val iterator = acorns.iterator()
        while (iterator.hasNext()) {
            val acorn = iterator.next()
            if (pete.collisionRectangle.overlaps(acorn.collisionrectangle)) {
                iterator.remove()
                score++
                info { "Score: $score" }
            }
        }
    }

    private fun handlePeteCollision() {
        val peteCells = filterOutNonTiledCells(whichCellsDoesPeteCover())
        if (peteCells.size == 0) return

        val isStuckInTheWall = (peteCells.size == 2) && (peteCells[0].cellX == peteCells[1].cellX)

        for (cell in peteCells) {
            val cellLevelX = cell.cellX * CELL_SIZE
            val cellLevelY = cell.cellY * CELL_SIZE
            val cellRectangle = Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE)
            val intersection = Rectangle()
            Intersector.intersectRectangles(pete.collisionRectangle, cellRectangle, intersection)

            if (isStuckInTheWall) {
                if (intersection.x == pete.position.x) {
                    pete.position.set(intersection.getX() + intersection.getWidth(), pete.position.y)
                }

                if (intersection.x > pete.position.x) {
                    pete.position.set(intersection.x - PETE_WIDTH, pete.position.y)
                }
                return
            }

            if ((intersection.height < intersection.width && pete.jumpState != JUMPING) ||
                    (intersection.height < intersection.width && pete.jumpState == JUMPING
                            && pete.velocity.y < 0)) {
                pete.position.set(pete.position.x, intersection.y + intersection.height)
                pete.jumpState = GROUNDED
                pete.velocity.y = 0f

            } else if (intersection.width < intersection.height) {
                if (intersection.x == pete.position.x) {
                    pete.position.set(intersection.getX() + intersection.getWidth(), pete.position.y)
                }

                if (intersection.x > pete.position.x) {
                    pete.position.set(intersection.x - PETE_WIDTH, pete.position.y)
                }
            }
        }
    }

    private fun whichCellsDoesPeteCover(): Array<CollisionCell> {
        val x = pete.position.x
        val y = pete.position.y
        val cellsCovered: Array<CollisionCell> = Array()
        val cellX = x / CELL_SIZE
        val cellY = y / CELL_SIZE
        val bottomLeftCellX = MathUtils.floor(cellX)
        val bottomLeftCellY = MathUtils.floor(cellY)
        val tiledMapTileLayer = tiledMap.layers.get(0) as TiledMapTileLayer

        cellsCovered.add(CollisionCell(
                tiledMapTileLayer.getCell(bottomLeftCellX, bottomLeftCellY),
                bottomLeftCellX,
                bottomLeftCellY))

        if (cellX % 1 != 0f && cellY % 1 != 0f) {
            val topRightCellX = bottomLeftCellX + 1
            val topRightCellY = bottomLeftCellY + 1

            cellsCovered.add(CollisionCell(
                    tiledMapTileLayer.getCell(topRightCellX, topRightCellY),
                    topRightCellX,
                    topRightCellY))
        }

        if (cellX % 1 != 0f) {
            val bottomRightCellX = bottomLeftCellX + 1
            cellsCovered.add(CollisionCell(tiledMapTileLayer.getCell(bottomRightCellX, bottomLeftCellY),
                    bottomRightCellX,
                    bottomLeftCellY))
        }
        if (cellY % 1 != 0f) {
            val topLeftCellY = bottomLeftCellY + 1
            cellsCovered.add(CollisionCell(tiledMapTileLayer.getCell(bottomLeftCellX, topLeftCellY),
                    bottomLeftCellX,
                    topLeftCellY))
        }
        return cellsCovered
    }

    private fun filterOutNonTiledCells(cells: Array<CollisionCell>): Array<CollisionCell> {
        val iterator = cells.iterator()
        while (iterator.hasNext()) {
            val collisionCell = iterator.next()
            if (collisionCell.isEmpty) iterator.remove()
        }

        return cells
    }

    private fun populateAcorns() {
        val mapLayer = tiledMap.layers.get(ACORN_LAYER)
        for (mapObject in mapLayer.objects)
            acorns.add(Acorn(assetManager.get(ACORN_FILE_NAME, Texture::class.java),
                    Vector2(mapObject.properties.get<Float>("x", Float::class.java),
                            mapObject.properties.get<Float>("y", Float::class.java))))
    }

    private fun draw() {
        batch.projectionMatrix = camera.projection
        batch.transformMatrix = camera.view
        orthogonalTiledMapRenderer.render()
        batch.use {
            for (acorn in acorns) acorn.draw(it)
            pete.draw(it)
        }
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

    private inner class CollisionCell(
            private val cell: TiledMapTileLayer.Cell?,
            val cellX: Int,
            val cellY: Int) {

        val isEmpty: Boolean
            get() = cell == null
    }

}