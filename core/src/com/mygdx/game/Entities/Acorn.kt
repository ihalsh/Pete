package com.mygdx.game.Entities

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.Utils.Constants.Companion.ACORN_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.ACORN_WIDTH

class Acorn(private val texture: Texture,
            private val position: Vector2 = Vector2(),
            val collisionrectangle: Rectangle = Rectangle(
                    position.x,
                    position.y,
                    ACORN_WIDTH,
                    ACORN_HEIGHT)) {

    fun draw(batch: Batch) {
        batch.draw(texture, position.x, position.y)
    }
}