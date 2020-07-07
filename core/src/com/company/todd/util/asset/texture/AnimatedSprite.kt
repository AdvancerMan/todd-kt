package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle

enum class AnimationType {
    STAY, RUN, JUMP, FALL, LANDING, SHOOT
}

class AnimatedSprite(private val animations: Map<AnimationType, Animation<TextureRegion>>): MySprite() {
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
            updateRegion()
        }
    }

    private fun updateRegion() {
        val region = playingNow.getKeyFrame(elapsed)
        setRegion(region)
        setSize(region.regionWidth.toFloat(), region.regionHeight.toFloat())
        setOrigin(width / 2, height / 2)
    }

    override fun update(delta: Float) {
        val ind1 = playingNow.getKeyFrameIndex(elapsed)
        elapsed += delta
        val ind2 = playingNow.getKeyFrameIndex(elapsed)
        if (ind1 != ind2) {
            updateRegion()
        }
    }

    override fun isAnimationFinished() = playingNow.isAnimationFinished(elapsed)
}
