package com.company.todd.util.asset.texture.sprite

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

enum class AnimationType {
    STAY, RUN, JUMP, FALL, LANDING, SHOOT
}

abstract class AnimatedSprite : MySprite {
    protected lateinit var playingNow: Animation<TextureRegion>
    protected var elapsed = 0f

    constructor(playingNow: Animation<TextureRegion>) : super() {
        this.playingNow = playingNow
    }

    constructor() : super()

    override fun setPlayingType(type: AnimationType, forceReset: Boolean) {
        if (forceReset || type != playingType) {
            super.setPlayingType(type, true)
            elapsed = 0f
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
}
