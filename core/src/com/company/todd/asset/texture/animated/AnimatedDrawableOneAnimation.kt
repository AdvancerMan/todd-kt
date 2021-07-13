package com.company.todd.asset.texture.animated

import com.badlogic.gdx.graphics.g2d.Animation
import com.company.todd.asset.texture.AnimationInfo
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.TextureManager

class AnimatedDrawableOneAnimation(private val animationInfo: AnimationInfo,
                                   animation: Animation<MyDrawable>) : AnimatedDrawable(animation) {
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
