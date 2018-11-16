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
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.mygdx.game.Utils.Constants.Companion.ANIMATION_DURATION
import com.mygdx.game.Utils.Constants.Companion.PETE_FILE_NAME
import com.mygdx.game.Utils.Constants.Companion.PETE_HEIGHT
import com.mygdx.game.Utils.Constants.Companion.PETE_WIDTH

object Assets : Disposable, AssetErrorListener {

    val assetManager: AssetManager = AssetManager().apply {setErrorListener(Assets)
        setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))}

    private val logger = logger<Assets>()
    lateinit var tiledMap: TiledMap
    private lateinit var peteTexture: Texture
    lateinit var standing: TextureRegion
    lateinit var jumpUp: TextureRegion
    lateinit var jumpDown: TextureRegion
    lateinit var walking: Animation<TextureRegion>

    fun loadAssets() {
        with(assetManager) {
            logger.level = Logger.INFO
            load(MAP_FILE_NAME, TiledMap::class.java)
            load(PETE_FILE_NAME, Texture::class.java)
            finishLoading()
            tiledMap = get(MAP_FILE_NAME)
            peteTexture = get(PETE_FILE_NAME)
        }

        val regions = TextureRegion.split(
                peteTexture,
                PETE_WIDTH.toInt(),
                PETE_HEIGHT.toInt())[0]

        walking = Animation(ANIMATION_DURATION, regions[0], regions[1]).apply { playMode = LOOP }
        standing = regions[0]
        jumpUp = regions[2]
        jumpDown = regions[3]
    }

    override fun dispose() {
        assetManager.dispose()
        info { "Assets disposed...Ok" }
    }

    override fun error(asset: AssetDescriptor<*>, throwable: Throwable) {
        logger.error(throwable) { "Couldn't load asset: ${asset.fileName}" }
    }
}