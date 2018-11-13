package com.mygdx.game

import com.badlogic.gdx.Screen
import com.mygdx.game.Utils.Assets
import com.mygdx.game.Utils.Assets.assetManager
import ktx.app.KtxGame

class PeteGame : KtxGame<Screen>() {

    override fun create() {

        Assets.loadAssets()

        addScreen(LoadingScreen(this))
        addScreen(GameScreen(this))

        setScreen<LoadingScreen>()
    }


    override fun dispose() {
        assetManager.dispose()
    }
}
