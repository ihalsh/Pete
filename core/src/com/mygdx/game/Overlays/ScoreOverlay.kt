package com.mygdx.game.Overlays

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.mygdx.game.Utils.Constants.Companion.HUD_MARGIN
import com.mygdx.game.Utils.Constants.Companion.HUD_SIZE

class ScoreOverlay(val viewport: ExtendViewport = ExtendViewport(HUD_SIZE, HUD_SIZE)) {

    private var font: BitmapFont = BitmapFont()

    fun render(batch: SpriteBatch, score: Int) {

        // Apply the viewport
        viewport.apply()

        // Set the projection matrix and camera
//        batch.projectionMatrix = viewport.camera.combined //Don't work when enabled!
        batch.transformMatrix = viewport.camera.view

        font.color = Color.WHITE
        font.draw(batch,
                "Score: $score",
                HUD_MARGIN,
                viewport.worldHeight - HUD_MARGIN,
                0f,
                Align.left,
                false)
    }
}