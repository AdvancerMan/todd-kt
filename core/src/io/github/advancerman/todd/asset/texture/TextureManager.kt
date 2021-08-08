package io.github.advancerman.todd.asset.texture

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.GdxRuntimeException
import io.github.advancerman.todd.asset.AssetManager
import io.github.advancerman.todd.asset.texture.animated.AnimatedDrawableManyAnimations
import io.github.advancerman.todd.asset.texture.animated.AnimatedDrawableOneAnimation
import io.github.advancerman.todd.asset.texture.static.CoveredTiledDrawable
import io.github.advancerman.todd.asset.texture.static.ToddTextureRegionDrawable
import io.github.advancerman.todd.asset.texture.static.NineTiledDrawable
import io.github.advancerman.todd.asset.texture.static.TransformTiledDrawable
import io.github.advancerman.todd.json.deserialization.loadTextureInfos
import kotlin.random.Random

class TextureManager : AssetManager<Texture>(Texture::class.java) {
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
            } catch (e: GdxRuntimeException) {
                error("Error while loading $fileName texture: ", e)
                createAdditionalTexture()
            }

    private fun load(info: AnimationPackInfo) =
            info.animations.associate { it.first to load(it.second) }

    private fun load(info: AnimationInfo) =
            Animation(
                    info.frameDuration,
                    Array(
                            info.bounds.map {
                                loadDrawable(
                                        info.frameInfo.copy(
                                                it.x.toInt(), it.y.toInt(),
                                                it.width.toInt(), it.height.toInt()
                                        )
                                )
                            }.toTypedArray()
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
        if (info.frameInfo.path != additionalInfo.path) {
            unload(info.frameInfo.path, info.bounds.size)
        }
    }

    fun unload(info: RegionInfo) {
        if (info.path != additionalInfo.path) {
            unload(info.path)
        }
    }

    private fun loadDrawable(info: TextureInfo): ToddDrawable =
            when (info) {
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
                    ToddTextureRegionDrawable(info, load(info))
                }
                is AnimationInfo -> {
                    AnimatedDrawableOneAnimation(info, load(info))
                }
                is AnimationPackInfo -> {
                    AnimatedDrawableManyAnimations(info, load(info))
                }
            }

    fun loadDrawable(name: String) =
            infos[name]?.let { loadDrawable(it) }
                    ?: ToddTextureRegionDrawable(additionalInfo, additionalTexture)
                            .also { error("Trying to load texture that doesn't exist in infos: $name") }

    override fun dispose() {
        additionalTexture.texture.dispose()
        super.dispose()
    }
}
