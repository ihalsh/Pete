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
        const val CELL_SIZE = 16f

        //Pete
        val SPAWN_POSITION = Vector2(0f, 96f)
        const val PETE_WIDTH = 16f
        const val PETE_HEIGHT = 15f
        const val JUMP_SPEED = 300f
        const val MAX_JUMP_DURATION = 0.15f
        const val ANIMATION_DURATION = 0.25f
        const val GRAVITY = 1500
        val MOVEMENT_SPEED = Vector2(100f, 0f)

        //Acorn
        const val ACORN_WIDTH = 16f
        const val ACORN_HEIGHT = 16f
        const val ACORN_LAYER = "Acorns"
        const val ACORN_FILE_NAME = "acorn.png"
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