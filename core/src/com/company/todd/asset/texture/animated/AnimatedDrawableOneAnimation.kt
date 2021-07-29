package com.company.todd.asset.texture.animated

import com.badlogic.gdx.graphics.g2d.Animation
import com.company.todd.asset.texture.AnimationInfo
import com.company.todd.asset.texture.ToddDrawable
import com.company.todd.asset.texture.TextureManager

class AnimatedDrawableOneAnimation(private val animationInfo: AnimationInfo,
                                   animation: Animation<ToddDrawable>) : AnimatedDrawable(animation) {
    override fun setPlayingType(type: AnimationType, forceReset: Boolean) {
        super.setPlayingType(type, forceReset)
        if (forceReset) {
            playingNow = playingNow
        }
    }

    override fun dispose(manager: TextureManager) {
        manager.unload(animationInfo)
    }
}
