package com.mygdx.game

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.mygdx.game.Entities.Pete
import com.mygdx.game.Utils.Assets
import com.mygdx.game.Utils.Constants
import com.mygdx.game.Utils.Constants.Companion.CELL_SIZE
import com.mygdx.game.Utils.Constants.Companion.PETE_WIDTH
import com.mygdx.game.Utils.Constants.Companion.WORLD_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import com.mygdx.game.Utils.Constants.JumpState.GROUNDED
import com.mygdx.game.Utils.Constants.JumpState.JUMPING
import ktx.app.KtxScreen
import ktx.log.info


class GameScreen(private val pete: Pete = Pete()) : KtxScreen {

    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera)
    private val tiledMap = Assets.tiledMap
    private val orthogonalTiledMapRenderer = OrthogonalTiledMapRenderer(tiledMap, batch)

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

        pete.update(delta)
        handlePeteCollision()
        pete.stopPeteLeavingTheScreen()

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

    private fun draw() {
        batch.projectionMatrix = camera.projection
        batch.transformMatrix = camera.view
        orthogonalTiledMapRenderer.render()
        pete.draw(batch)
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