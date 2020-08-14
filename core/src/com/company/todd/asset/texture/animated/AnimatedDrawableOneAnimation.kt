package com.company.todd.asset.texture.animated

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.company.todd.asset.texture.AnimationInfo
import com.company.todd.asset.texture.TextureManager

class AnimatedDrawableOneAnimation(private val animationInfo: AnimationInfo,
                                   animation: Animation<TextureRegion>) : AnimatedDrawable(animation) {
    override fun dispose(manager: TextureManager) {
        manager.unload(animationInfo)
    }
}
