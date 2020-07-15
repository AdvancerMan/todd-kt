package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.company.todd.util.asset.AssetManager
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.random.Random

class TextureManager: AssetManager<Texture>(Texture::class.java) {
    private val additionalTexture = TextureRegion(createAdditionalTexture())
    private val additionalTextureInfo = RegionInfo("__additionalTexture", 0, 0, 10, 10)
    private val regionInfos: Map<String, RegionInfo>
    private val animationInfos: Map<String, AnimationInfo>
    private val animationPackInfos: Map<String, AnimationPackInfo>

    init {
        loadTextureInfos().let {
            regionInfos = it.first
            animationInfos = it.second
            animationPackInfos = it.third
        }
    }

    private fun createAdditionalTexture() =
            Texture(
                    Pixmap(10, 10, Pixmap.Format.RGBA8888)
                            .apply {
                                when (Random.nextInt(3)) {
                                    0 -> setColor(1f, 0f, 0f, 1f)
                                    1 -> setColor(0f, 1f, 0f, 1f)
                                    2 -> setColor(0f, 0f, 1f, 1f)
                                }
                                fill()
                            }
            )

    override fun loadAsset(fileName: String) =
            try {
                Texture(fileName)
            } catch(e: GdxRuntimeException) {
                error("Error while loading $fileName texture:\n" +
                        StringWriter().let {
                            e.printStackTrace(PrintWriter(it))
                            it.toString()
                        })
                createAdditionalTexture()
            }

    private fun load(info: AnimationPackInfo): Map<AnimationType, Animation<TextureRegion>> =
            info.animations.associate { it.first to load(it.second) }

    private fun load(info: AnimationInfo): Animation<TextureRegion> =
            Animation(
                    info.frameDuration,
                    Array(
                            info.bounds
                                    .map {
                                        load(RegionInfo(
                                                info.path, it.x.toInt(), it.y.toInt(),
                                                it.width.toInt(), it.height.toInt()
                                        ))
                                    }
                                    .toTypedArray()
                    ),
                    info.mode
            )

    private fun load(info: RegionInfo) =
            TextureRegion(load(info.path), info.x, info.y, info.w, info.h)

    fun unload(info: AnimationPackInfo) {
        info.animations.forEach { unload(it.second) }
    }

    fun unload(info: AnimationInfo) {
        unload(info.path, info.bounds.size)
    }

    fun unload(info: RegionInfo) {
        if (info.path != additionalTextureInfo.path) {
            unload(info.path)
        }
    }

    fun loadSprite(name: String) =
            when (name) {
                in regionInfos.keys -> regionInfos[name]!!.let {
                    StaticSprite(it, load(it))
                }
                in animationInfos.keys -> animationInfos[name]!!.let {
                    AnimatedSpriteOneAnimation(it, load(it))
                }
                in animationPackInfos.keys -> animationPackInfos[name]!!.let {
                    AnimatedSpriteManyAnimations(it, load(it))
                }
                else -> {
                    error("Trying to load texture that doesn't exist in infos: $name")
                    StaticSprite(additionalTextureInfo, additionalTexture)
                }
            }

    override fun dispose() {
        additionalTexture.texture.dispose()
        super.dispose()
    }
}
