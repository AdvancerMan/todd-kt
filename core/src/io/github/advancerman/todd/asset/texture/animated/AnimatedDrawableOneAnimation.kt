package io.github.advancerman.todd.asset.texture.animated

import com.badlogic.gdx.graphics.g2d.Animation
import io.github.advancerman.todd.asset.texture.AnimationInfo
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.asset.texture.TextureManager

class AnimatedDrawableOneAnimation(private val animationInfo: AnimationInfo,
                                   animation: Animation<ToddDrawable>) : AnimatedDrawable(animation) {
    override fun dispose(manager: TextureManager) {
        manager.unload(animationInfo)
    }
}
