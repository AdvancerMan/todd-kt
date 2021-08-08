package io.github.advancerman.todd.asset.texture

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable
import io.github.advancerman.todd.asset.texture.animated.AnimationType
import io.github.advancerman.todd.json.JsonFullSerializable

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

interface ToddDrawable : FlipTransformDrawable, DisposableByManager, WithZIndex {
    @JsonFullSerializable("name")
    var drawableName: String?

    @JsonFullSerializable("zIndex")
    override var myZIndex: Int

    @JsonFullSerializable
    val size: Vector2

    @JsonFullSerializable
    val offset: Vector2

    fun update(delta: Float) {}

    // for animations
    fun hasAnimationType(type: AnimationType) = false
    fun setPlayingType(type: AnimationType, forceReset: Boolean = false) {}
    fun getPlayingType(): AnimationType = AnimationType.STAY
    fun isAnimationFinished() = true
}

fun ToddDrawable.withMinSize() = apply {
    minWidth = size.x
    minHeight = size.y
}
