package com.company.todd.util.asset.texture.sprite

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.company.todd.util.asset.texture.AnimationInfo
import com.company.todd.util.asset.texture.TextureManager

class AnimatedSpriteOneAnimation(private val animationInfo: AnimationInfo,
                                 animation: Animation<TextureRegion>) : AnimatedSprite(animation) {
    override fun dispose(manager: TextureManager) {
        manager.unload(animationInfo)
    }
}
