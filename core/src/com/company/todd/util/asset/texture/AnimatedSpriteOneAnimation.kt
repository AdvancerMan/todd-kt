package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class AnimatedSpriteOneAnimation(private val animationInfo: AnimationInfo,
                                 animation: Animation<TextureRegion>) : AnimatedSprite(animation) {
    override fun dispose(manager: TextureManager) {
        manager.unload(animationInfo)
    }
}
