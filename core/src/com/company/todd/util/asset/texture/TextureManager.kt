package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.company.todd.util.asset.AssetManager

class TextureManager: AssetManager<Texture>(Texture::class.java) {
    override fun loadAsset(fileName: String) = Texture(fileName)

    fun loadAnimationPack(info: AnimationPackInfo): Map<AnimationType, Animation<TextureRegion>> =
            info.animations.associate { it.first to loadAnimation(it.second) }

    fun loadAnimation(info: AnimationInfo): Animation<TextureRegion> =
            Animation(
                    info.frameDuration,
                    Array(
                            info.bounds
                                    .map {
                                        loadTextureRegion(RegionInfo(
                                                info.path, it.x.toInt(), it.y.toInt(),
                                                it.width.toInt(), it.height.toInt()
                                        ))
                                    }
                                    .toTypedArray()
                    ),
                    info.mode
            )

    fun loadTextureRegion(info: RegionInfo) =
            TextureRegion(load(info.path), info.x, info.y, info.w, info.h)

    fun unloadAnimationPack(info: AnimationPackInfo) =
            info.animations.forEach { unloadAnimation(it.second) }

    fun unloadAnimation(info: AnimationInfo) =
            unload(info.path, info.bounds.size)

    fun unloadTextureRegion(info: RegionInfo) =
            unload(info.path)
}
