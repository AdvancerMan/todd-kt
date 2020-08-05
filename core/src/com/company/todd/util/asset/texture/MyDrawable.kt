package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable
import com.company.todd.util.asset.texture.animated.AnimationType

interface DisposableByManager {
    fun dispose(manager: TextureManager)
}

interface MyDrawableI : DisposableByManager {
    fun update(delta: Float) {}

    // for animations
    fun hasAnimationType(type: AnimationType) = false
    fun setPlayingType(type: AnimationType, forceReset: Boolean = false) {}
    fun getPlayingType(): AnimationType = AnimationType.STAY
    fun isAnimationFinished() = true
}

interface FlipTransformDrawable : TransformDrawable {
    fun draw(
            batch: Batch, x: Float, y: Float,
            originX: Float, originY: Float,
            width: Float, height: Float,
            scaleX: Float, scaleY: Float, rotation: Float,
            flipX: Boolean, flipY: Boolean
    )
}

interface MyDrawable : FlipTransformDrawable, MyDrawableI
