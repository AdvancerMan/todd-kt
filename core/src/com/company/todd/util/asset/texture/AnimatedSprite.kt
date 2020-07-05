package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle

enum class AnimationType {
    STAY, RUN, JUMP, FALL, LANDING, SHOOT
}

class AnimatedSprite(private val animations: Map<AnimationType, Animation<TextureRegion>>): Sprite() {
    private var elapsed = 0f
    private lateinit var playingNow: Animation<TextureRegion>
    private lateinit var playingNowType: AnimationType

    init {
        setPlayingType(AnimationType.STAY, true)
    }

    fun setPlayingType(type: AnimationType, forceReset: Boolean = false) {
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

    fun update(delta: Float) {
        val ind1 = playingNow.getKeyFrameIndex(elapsed)
        elapsed += delta
        val ind2 = playingNow.getKeyFrameIndex(elapsed)
        if (ind1 != ind2) {
            updateRegion()
        }
    }

    fun draw(centerX: Float, centerY: Float, batch: Batch, cameraRectangle: Rectangle) {
        setCenter(centerX, centerY)
        if (cameraRectangle.overlaps(boundingRectangle)) {
            super.draw(batch)
        }
    }

    fun isAnimationFinished() = playingNow.isAnimationFinished(elapsed)
}
