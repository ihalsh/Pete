package com.mygdx.game.Entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils
import com.mygdx.game.Utils.Assets.jumpUp
import com.mygdx.game.Utils.Assets.standing
import com.mygdx.game.Utils.Assets.walking
import com.mygdx.game.Utils.Constants.*
import com.mygdx.game.Utils.Constants.Companion.GRAVITY
import com.mygdx.game.Utils.Constants.Companion.JUMP_SPEED
import com.mygdx.game.Utils.Constants.Companion.MAX_JUMP_DURATION
import com.mygdx.game.Utils.Constants.Companion.MOVEMENT_SPEED
import com.mygdx.game.Utils.Constants.Companion.PETE_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.PETE_WIDTH
import com.mygdx.game.Utils.Constants.Companion.SPAWN_POSITION
import com.mygdx.game.Utils.Constants.Companion.WORLD_WIDTH
import com.mygdx.game.Utils.Constants.JumpState.*
import com.mygdx.game.Utils.Constants.WalkState.WALKING
import ktx.graphics.use

class Pete(val position: Vector2 = Vector2(SPAWN_POSITION),
           val velocity: Vector2 = Vector2(),
           var jumpState: JumpState = FALLING,
           var facing: Facing = Facing.RIGHT,
           val collisionRectangle: Rectangle = Rectangle(0f, 0f, PETE_WIDTH, PETE_HEIGHT),
           private var jumpStartTime: Long = 5,
           private var walkStartTime: Long = 0,
           private var walkState: WalkState = WalkState.STANDING) {

    fun update(delta: Float) {

        // Accelerate Pete down
        velocity.y -= delta * GRAVITY

        // Apply Pete velocity to his position
        position.mulAdd(velocity, delta)

        // If Pete isn't JUMPING, make him now FALLING
        if (jumpState != JUMPING && jumpState != GROUNDED) {
            jumpState = JumpState.FALLING
            if (position.y < 0) {
                jumpState = GROUNDED
                velocity.y = 0f
            }
        }

        // Move left/right
        val left: Boolean = Gdx.input.isKeyPressed(Input.Keys.LEFT)
        val right = Gdx.input.isKeyPressed(Input.Keys.RIGHT)

        when {
            (left && !right) -> moveLeft(delta)
            (right && !left) -> moveRight(delta)
            else -> walkState = WalkState.STANDING
        }

        // Jump
        if (Gdx.input.isKeyPressed(Input.Keys.UP) /*&&
                MathUtils.nanoToSec * TimeUtils.timeSinceNanos(jumpStartTime) > JUMP_DELAY*/) {
            // Handle jump key
            when (jumpState) {
                GROUNDED -> startJump()
                JUMPING -> continueJump()
                else -> endJump()
            }
        }
        stopPeteLeavingTheScreen()
        updateCollisionRectangle()
    }

    private fun moveLeft(delta: Float) {
        // If we're GROUNDED and not WALKING, save the walkStartTime
        if (jumpState == GROUNDED && walkState != WALKING) walkStartTime = TimeUtils.nanoTime()

        //Set walkState to WALKING
        walkState = WALKING

        // Update facing direction
        facing = Facing.LEFT

        // Move Pete left by delta * movement speed
        position.mulAdd(MOVEMENT_SPEED, -delta)


    }

    private fun moveRight(delta: Float) {
        // If we're GROUNDED and not WALKING, save the walkStartTime
        if (jumpState == GROUNDED && walkState != WALKING) walkStartTime = TimeUtils.nanoTime()

        //Set walkState to WALKING
        walkState = WALKING

        // Update facing direction
        facing = Facing.RIGHT

        // Same for moving Pete right
        position.mulAdd(MOVEMENT_SPEED, delta)

        stopPeteLeavingTheScreen()
    }

    private fun startJump() {
        // Set jumpState to JUMPING
        jumpState = JUMPING

        // Set the jump start time
        jumpStartTime = TimeUtils.nanoTime()

        // Call continueJump()
        continueJump()

    }

    private fun continueJump() {
        // First, check if we're JUMPING, if not, just return
        if (jumpState != JUMPING) return

        // Find out how long we've been jumping
        val jumpElapsedTime = MathUtils.nanoToSec * TimeUtils.timeSinceNanos(jumpStartTime)

        if (jumpElapsedTime < MAX_JUMP_DURATION) {
            velocity.y = JUMP_SPEED

        } else endJump()

    }

    private fun endJump() {
        // If we're JUMPING, now we're FALLING
        if (jumpState == JUMPING) {
            jumpState = FALLING
        }
    }

    fun stopPeteLeavingTheScreen() {
        when {
            position.y < 0f -> {
                position.y = 0f
            }
            position.x < 0f -> position.x = 0f
            position.x + PETE_WIDTH > WORLD_WIDTH -> position.x = WORLD_WIDTH - PETE_WIDTH
        }
    }

    fun draw(batch: Batch) {
        // Select the correct sprite based on facing, jumpState, and walkState
        val region: TextureRegion = if (jumpState == GROUNDED) {
            if (walkState == WALKING) {
                // Calculate how long we've been walking in seconds
                val walkTimeSeconds = MathUtils.nanoToSec * (TimeUtils.nanoTime() - walkStartTime)
                // Select the correct frame from the walking right animation
                walking.getKeyFrame(walkTimeSeconds)
            } else {
                standing
            }
        } else {
            //if (jumpState == JUMPING) {
            jumpUp
            //} else jumpDown
        }
        if (facing == Facing.RIGHT && region.isFlipX) region.flip(true, false)
        if (facing == Facing.LEFT && !region.isFlipX) region.flip(true, false)

        batch.use {
            it.draw(
                    region,
                    position.x,
                    position.y
//                    0f,
//                    0f,
//                    PETE_WIDTH,
//                    PETE_HEIGHT,
//                    3f,
//                    3f,
//                    0f
            )
        }
    }

    fun updateCollisionRectangle() =
            collisionRectangle.set(position.x, position.y, PETE_WIDTH, PETE_HEIGHT)

    fun drawDebug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.rect(
                collisionRectangle.x,
                collisionRectangle.y,
                collisionRectangle.width,
                collisionRectangle.height
        )
    }
}