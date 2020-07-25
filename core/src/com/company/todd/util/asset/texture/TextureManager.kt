package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import com.company.todd.util.asset.AssetManager
import com.company.todd.util.asset.texture.drawable.CoveredTiledDrawable
import com.company.todd.util.asset.texture.drawable.NineTiledDrawable
import com.company.todd.util.asset.texture.drawable.TransformTiledDrawable
import com.company.todd.util.asset.texture.drawable.toMyDrawable
import com.company.todd.util.asset.texture.sprite.AnimatedSpriteManyAnimations
import com.company.todd.util.asset.texture.sprite.AnimatedSpriteOneAnimation
import com.company.todd.util.asset.texture.sprite.AnimationType
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.random.Random

class TextureManager: AssetManager<Texture>(Texture::class.java) {
    private val additionalTexture = TextureRegion(createAdditionalTexture())
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
        unload(info.path)
    }

    fun loadSprite(name: String) =
            infos[name].let { info ->
                when (info) {
                    is TiledRegionInfo -> {
                        TransformTiledDrawable(load(info)).toMyDrawable({ mng -> mng.unload(info) })
                    }
                    is CoveredTiledRegionInfo -> {
                        load(info).let {
                            CoveredTiledDrawable(
                                    TextureRegion(it, 0, it.regionHeight - info.uh, it.regionWidth, info.uh),
                                    it.apply { regionHeight -= info.uh }
                            )
                        }.toMyDrawable({ mng -> mng.unload(info) })
                    }
                    is NineTiledRegionInfo -> {
                        NineTiledDrawable(load(info), info.lw, info.rw, info.uh, info.dh).toMyDrawable({ mng -> mng.unload(info) })
                    }
                    is RegionInfo -> {
                        TextureRegionDrawable(load(info)).toMyDrawable({ mng -> mng.unload(info) })
                    }
                    is AnimationInfo -> {
                        AnimatedSpriteOneAnimation(info, load(info)).toMyDrawable()
                    }
                    is AnimationPackInfo -> {
                        AnimatedSpriteManyAnimations(info, load(info)).toMyDrawable()
                    }
                    else -> {
                        error("Trying to load texture that doesn't exist in infos: $name")
                        TextureRegionDrawable(additionalTexture).toMyDrawable({})
                    }
                }
            }

    override fun dispose() {
        additionalTexture.texture.dispose()
        super.dispose()
    }
}
