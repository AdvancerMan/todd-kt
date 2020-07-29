package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.company.todd.util.asset.AssetManager
import com.company.todd.util.asset.texture.animated.AnimatedDrawableManyAnimations
import com.company.todd.util.asset.texture.animated.AnimatedDrawableOneAnimation
import com.company.todd.util.asset.texture.animated.AnimationType
import com.company.todd.util.asset.texture.static.CoveredTiledDrawable
import com.company.todd.util.asset.texture.static.MyTextureRegionDrawable
import com.company.todd.util.asset.texture.static.NineTiledDrawable
import com.company.todd.util.asset.texture.static.TransformTiledDrawable
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.random.Random

class TextureManager: AssetManager<Texture>(Texture::class.java) {
    private val additionalTexture = TextureRegion(createAdditionalTexture())
    private val additionalInfo = RegionInfo("__additionalInfo", 0, 0, 10, 10)
    private val infos = loadTextureInfos()

    private fun createAdditionalTexture() =
            Texture(
                    Pixmap(10, 10, Pixmap.Format.RGBA8888)
                            .apply {
                                when (Random.nextInt(9)) {
                                    0 -> setColor(1f, 0f, 0f, 1f)
                                    1 -> setColor(0f, 1f, 0f, 1f)
                                    2 -> setColor(0f, 0f, 1f, 1f)
                                    3 -> setColor(1f, 1f, 0f, 1f)
                                    4 -> setColor(0f, 1f, 1f, 1f)
                                    5 -> setColor(1f, 0f, 1f, 1f)
                                    6 -> setColor(1f, 0.5f, 0.5f, 1f)
                                    7 -> setColor(0.5f, 1f, 0.5f, 1f)
                                    8 -> setColor(0.5f, 0.5f, 1f, 1f)
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
            load(info.path).let {
                TextureRegion(it, info.x, it.height - info.y - info.h, info.w, info.h)
            }

    fun unload(info: AnimationPackInfo) {
        info.animations.forEach { unload(it.second) }
    }

    fun unload(info: AnimationInfo) {
        unload(info.path, info.bounds.size)
    }

    fun unload(info: RegionInfo) {
        if (info.path != additionalInfo.path) {
            unload(info.path)
        }
    }

    fun loadDrawable(name: String): MyDrawable =
            when (val info = infos[name]) {
                is TiledRegionInfo -> {
                    TransformTiledDrawable(info, load(info))
                }
                is CoveredTiledRegionInfo -> {
                    CoveredTiledDrawable(info, load(info))
                }
                is NineTiledRegionInfo -> {
                    NineTiledDrawable(info, load(info))
                }
                is RegionInfo -> {
                    MyTextureRegionDrawable(info, load(info))
                }
                is AnimationInfo -> {
                    AnimatedDrawableOneAnimation(info, load(info))
                }
                is AnimationPackInfo -> {
                    AnimatedDrawableManyAnimations(info, load(info))
                }
                else -> {
                    error("Trying to load texture that doesn't exist in infos: $name")
                    MyTextureRegionDrawable(additionalInfo, additionalTexture)
                }
            }

    override fun dispose() {
        additionalTexture.texture.dispose()
        super.dispose()
    }
}
