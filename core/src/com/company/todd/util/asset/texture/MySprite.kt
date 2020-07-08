package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

abstract class MySprite: Sprite() {
    open fun setPlayingType(type: AnimationType, forceReset: Boolean = false) {}
    open fun isAnimationFinished() = false
    open fun update(delta: Float) {}

    protected fun updateRegion(region: TextureRegion) {
        setRegion(region)
        setSize(region.regionWidth.toFloat(), region.regionHeight.toFloat())
        setOriginCenter()
    }

    fun draw(center: Vector2, batch: Batch, cameraRectangle: Rectangle) {
        setCenter(center.x, center.y)
        if (cameraRectangle.overlaps(boundingRectangle)) {
            super.draw(batch)
        }
    }

    abstract fun dispose(manager: TextureManager)
}
