package com.mygdx.game.Entities

import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.Input.Keys.LEFT
import com.badlogic.gdx.Input.Keys.RIGHT
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.Utils.Constants.Companion.HEIGHT
import com.mygdx.game.Utils.Constants.Companion.MAX_X_SPEED
import com.mygdx.game.Utils.Constants.Companion.WIDTH
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import com.sun.awt.SecurityWarning.setPosition



class Pete {

    private val collisionRectangle = Rectangle(0f, 0f, WIDTH, HEIGHT)
    private val position = Vector2()
    private val velocity = Vector2()

    fun update() {

        when {
            input.isKeyPressed(RIGHT) -> velocity.x = MAX_X_SPEED
            input.isKeyPressed(LEFT) -> velocity.x = -MAX_X_SPEED
            else -> velocity.x = 0f
        }
        position.x += velocity.x
        position.y += velocity.y
        stopPeteLeavingTheScreen()
        updateCollisionRectangle()
    }

    private fun stopPeteLeavingTheScreen() {
        when {
            position.y < 0f -> position.y = 0f
            position.x < 0f -> position.x = 0f
            position.x + WIDTH > WORLD_WIDTH -> position.x = WORLD_WIDTH - WIDTH
        }
    }

    fun drawDebug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.rect(
                collisionRectangle.x,
                collisionRectangle.y,
                collisionRectangle.width,
                collisionRectangle.height
        )
    }

    private fun updateCollisionRectangle() {
        collisionRectangle.setPosition(position.x, position.y)
    }
}