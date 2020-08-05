package com.company.todd.util.asset.texture.animated

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.company.todd.util.asset.texture.AnimationPackInfo
import com.company.todd.util.asset.texture.TextureManager

class AnimatedDrawableManyAnimations
private constructor(private val animationPackInfo: AnimationPackInfo,
                    private val animations: Map<AnimationType, Animation<TextureRegion>>,
                    firstAnimation: Pair<AnimationType, Animation<TextureRegion>>) :
        AnimatedDrawable(firstAnimation.second) {
    private var type = firstAnimation.first

    constructor(animationPackInfo: AnimationPackInfo,
                animations: Map<AnimationType, Animation<TextureRegion>>) :
            this(
                    animationPackInfo, animations,
                    animations[AnimationType.STAY]?.let { AnimationType.STAY to it }
                            ?: animations.entries.firstOrNull()?.let { it.key to it.value }
                            ?: throw IllegalArgumentException("Animation packs without animations aren't allowed")
            )

    override fun setPlayingType(type: AnimationType, forceReset: Boolean) {
        if (forceReset || type != this.type) {
            animations[type]?.let {
                playingNow = it
                this.type = type
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

    override fun getPlayingType() = type

    override fun hasAnimationType(type: AnimationType) =
            animations.containsKey(type)

    override fun dispose(manager: TextureManager) {
        manager.unload(animationPackInfo)
    }
}
