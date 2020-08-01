package com.company.todd.util.asset.texture.animated

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.company.todd.util.asset.texture.static.MyTextureRegionDrawable

enum class AnimationType {
    STAY, RUN, JUMP, PRE_FALL, FALL, FALL_AFTER_GROUND, ACTION
}

abstract class AnimatedDrawable(playingNow: Animation<TextureRegion>) :
        MyTextureRegionDrawable(null, playingNow.getKeyFrame(0f)) {
    protected var elapsed = 0f
    protected var playingNow = playingNow
        set(value) {
            elapsed = 0f
            region = value.getKeyFrame(0f)
            field = value
        }

    override fun update(delta: Float) {
        super.update(delta)
        elapsed += delta
        region = playingNow.getKeyFrame(elapsed)
    }

    override fun isAnimationFinished() = playingNow.isAnimationFinished(elapsed)
}
