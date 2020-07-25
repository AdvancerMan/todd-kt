package com.company.todd.util.asset.texture.sprite

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.company.todd.util.asset.texture.TextureManager

abstract class MySprite: Sprite() {
    var playingType = AnimationType.STAY
        protected set
    var isDirectedToRight = true
        set(value) {
            if (value != field) {
                field = value
                flip(true, false)
            }
        }

    open fun setPlayingType(type: AnimationType, forceReset: Boolean = false) {
        playingType = type
    }

    open fun isAnimationFinished() = false
    open fun update(delta: Float) {}

    protected fun updateRegion(region: TextureRegion) {
        setRegion(region)
        setSize(region.regionWidth.toFloat(), region.regionHeight.toFloat())
        setOriginCenter()
        if (!isDirectedToRight) {
            flip(true, false)
        }
    }

    fun draw(center: Vector2, batch: Batch) {
        setCenter(center.x, center.y)
        super.draw(batch)
    }

    abstract fun dispose(manager: TextureManager)
}
