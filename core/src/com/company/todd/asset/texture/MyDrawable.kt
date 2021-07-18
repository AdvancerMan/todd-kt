package com.company.todd.asset.texture

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable
import com.company.todd.asset.texture.animated.AnimationType
import com.company.todd.json.JsonFullSerializable

interface DisposableByManager {
    fun dispose(manager: TextureManager)
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

interface MyDrawable : FlipTransformDrawable, DisposableByManager {
    @JsonFullSerializable
    var drawableName: String?

    fun update(delta: Float) {}

    // for animations
    fun hasAnimationType(type: AnimationType) = false
    fun setPlayingType(type: AnimationType, forceReset: Boolean = false) {}
    fun getPlayingType(): AnimationType = AnimationType.STAY
    fun isAnimationFinished() = true
}
