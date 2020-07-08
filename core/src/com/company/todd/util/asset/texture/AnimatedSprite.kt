package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle

enum class AnimationType {
    STAY, RUN, JUMP, FALL, LANDING, SHOOT
}

class AnimatedSprite private  constructor(private val animationPackInfo: AnimationPackInfo,
                                          private val animations: Map<AnimationType, Animation<TextureRegion>>): MySprite() {
    constructor(animationPackInfo: AnimationPackInfo, manager: TextureManager):
            this(animationPackInfo, manager.loadAnimationPack(animationPackInfo))


    private var elapsed = 0f
    private lateinit var playingNow: Animation<TextureRegion>
    private lateinit var playingNowType: AnimationType

    init {
        setPlayingType(AnimationType.STAY, true)
    }

    override fun setPlayingType(type: AnimationType, forceReset: Boolean) {
        if (forceReset || type != playingNowType) {
            elapsed = 0f
            playingNowType = type
            playingNow = animations[type] ?: error("Trying to get non-existing animation $type")
            updateRegion(playingNow.getKeyFrame(elapsed))
        }
    }

    override fun update(delta: Float) {
        val ind1 = playingNow.getKeyFrameIndex(elapsed)
        elapsed += delta
        val ind2 = playingNow.getKeyFrameIndex(elapsed)
        if (ind1 != ind2) {
            updateRegion(playingNow.getKeyFrame(elapsed))
        }
    }

    override fun isAnimationFinished() = playingNow.isAnimationFinished(elapsed)

    override fun dispose(manager: TextureManager) {
        manager.unloadAnimationPack(animationPackInfo)
    }
}
