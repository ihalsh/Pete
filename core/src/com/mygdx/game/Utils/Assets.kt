package com.mygdx.game.Utils

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Logger
import com.mygdx.game.Utils.Constants.Companion.MAP_FILE_NAME
import ktx.log.info
import ktx.log.logger

object Assets : Disposable, AssetErrorListener {

    val assetManager: AssetManager = AssetManager().apply {setErrorListener(Assets)
        setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))}

    private val logger = logger<Assets>()

    fun loadAssets() {
        with(assetManager) {
            logger.level = Logger.INFO
            load(MAP_FILE_NAME, TiledMap::class.java)
            finishLoading()
        }
    }

    override fun dispose() {
        assetManager.dispose()
        info { "Assets disposed...Ok" }
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        logger.error(throwable) { "Couldn't load asset: ${asset.fileName}" }
    }
}