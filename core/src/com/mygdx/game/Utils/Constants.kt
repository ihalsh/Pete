package com.mygdx.game.Utils

import com.badlogic.gdx.math.Vector2

class Constants {

    companion object {
        //General
        const val WORLD_WIDTH = 640f
        const val WORLD_HEIGHT = 480f
        const val PROGRESS_BAR_WIDTH = 100f
        const val PROGRESS_BAR_HEIGHT = 25f
        const val MAP_FILE_NAME = "pete.tmx"
        const val PETE_FILE_NAME = "pete.png"

        //Pete
        const val PETE_WIDTH = 16f
        const val PETE_HEIGHT = 15f
        const val JUMP_SPEED = 200f
        const val MAX_JUMP_DURATION = 0.15
        const val ANIMATION_DURATION = 0.25f
        const val GRAVITY = 1000
        val MOVEMENT_SPEED = Vector2(100f, 0f)
    }

    enum class Facing {
        LEFT, RIGHT
    }
    enum class JumpState {
        JUMPING, FALLING, GROUNDED, RECOILING
    }
    enum class WalkState {
        STANDING, WALKING
    }
}