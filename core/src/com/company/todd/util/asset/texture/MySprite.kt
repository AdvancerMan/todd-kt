package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle

open class MySprite: Sprite {
    constructor(): super()
    constructor(region: TextureRegion): super(region)

    open fun setPlayingType(type: AnimationType, forceReset: Boolean = false) {}
    open fun isAnimationFinished() = false
    open fun update(delta: Float) {}

    fun draw(centerX: Float, centerY: Float, batch: Batch, cameraRectangle: Rectangle) {
        setCenter(centerX, centerY)
        if (cameraRectangle.overlaps(boundingRectangle)) {
            super.draw(batch)
        }
    }
}
