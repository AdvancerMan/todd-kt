package com.company.todd.util.asset.texture.sprite

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.company.todd.util.asset.texture.AnimationPackInfo
import com.company.todd.util.asset.texture.TextureManager

class AnimatedSpriteManyAnimations(private val animationPackInfo: AnimationPackInfo,
                                   private val animations: Map<AnimationType, Animation<TextureRegion>>) :
        AnimatedSprite() {
    init {
        setPlayingType(AnimationType.STAY, true)
    }

    override fun setPlayingType(type: AnimationType, forceReset: Boolean) {
        if (forceReset || type != playingType) {
            animations[type]?.let {
                playingNow = it
                super.setPlayingType(type, true)
            } ?: Gdx.app.error(
                    "AnimatedSprite",
                    "Trying to get non-existing animation $type for "
                            + animationPackInfo.animations
                            .map {
                                it.second.path + it.second.bounds
                                        .take(4)
                                        .map { r -> listOf(r.x, r.y, r.width, r.height).map(Float::toInt) }
                            }
            )
        }
    }

    override fun dispose(manager: TextureManager) {
        manager.unload(animationPackInfo)
    }
}
