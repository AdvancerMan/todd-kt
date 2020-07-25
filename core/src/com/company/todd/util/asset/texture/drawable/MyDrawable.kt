package com.company.todd.util.asset.texture.drawable

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable
import com.company.todd.util.asset.texture.TextureManager
import com.company.todd.util.asset.texture.sprite.AnimationType
import com.company.todd.util.asset.texture.sprite.MySprite

interface MyDrawableI {
    fun update(delta: Float)
    fun dispose(manager: TextureManager)

    // for animations
    fun setPlayingType(type: AnimationType, forceReset: Boolean = false)
    fun getPlayingType(): AnimationType
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

fun TextureRegionDrawable.toMyDrawable(dispose: (TextureManager) -> Unit, update: (Float) -> Unit = {}) =
        toMyDrawable(dispose, { batch: Batch, x: Float, y: Float,
                                originX: Float, originY: Float,
                                width: Float, height: Float,
                                scaleX: Float, scaleY: Float, rotation: Float,
                                flipX: Boolean, flipY: Boolean ->
            region.flip(region.isFlipX != flipX, region.isFlipY != flipY)
            draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        }, update)

fun FlipTransformDrawable.toMyDrawable(dispose: (TextureManager) -> Unit, update: (Float) -> Unit = {}) =
        toMyDrawable(dispose, { batch: Batch, x: Float, y: Float,
                                originX: Float, originY: Float,
                                width: Float, height: Float,
                                scaleX: Float, scaleY: Float, rotation: Float,
                                flipX: Boolean, flipY: Boolean ->
            this.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation, flipX, flipY)
        }, update)

fun TransformDrawable.toMyDrawable(
        dispose: (TextureManager) -> Unit,
        flipDraw: (
                Batch, Float, Float, Float,
                Float, Float, Float, Float,
                Float, Float, Boolean, Boolean
        ) -> Unit,
        update: (Float) -> Unit = {}) =
        object : MyDrawable {
            override fun dispose(manager: TextureManager) = dispose(manager)
            override fun update(delta: Float) = update(delta)

            override fun setPlayingType(type: AnimationType, forceReset: Boolean) {}
            override fun getPlayingType() = AnimationType.STAY

            override fun setRightWidth(rightWidth: Float) {
                this@toMyDrawable.rightWidth = rightWidth
            }

            override fun setMinHeight(minHeight: Float) {
                this@toMyDrawable.minHeight = minHeight
            }

            override fun setBottomHeight(bottomHeight: Float) {
                this@toMyDrawable.bottomHeight = bottomHeight
            }

            override fun setTopHeight(topHeight: Float) {
                this@toMyDrawable.topHeight = topHeight
            }

            override fun setMinWidth(minWidth: Float) {
                this@toMyDrawable.minWidth = minWidth
            }

            override fun setLeftWidth(leftWidth: Float) {
                this@toMyDrawable.leftWidth = leftWidth
            }

            override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float, flipX: Boolean, flipY: Boolean) {
                flipDraw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation, flipX, flipY)
//                this@toMyDrawable.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation, flipX, flipY)
            }

            override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) {
                this@toMyDrawable.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
            }

            override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
                this@toMyDrawable.draw(batch, x, y, width, height)
            }

            override fun getLeftWidth() = this@toMyDrawable.leftWidth
            override fun getBottomHeight() = this@toMyDrawable.bottomHeight
            override fun getRightWidth() = this@toMyDrawable.rightWidth
            override fun getMinWidth() = this@toMyDrawable.minWidth
            override fun getTopHeight() = this@toMyDrawable.topHeight
            override fun getMinHeight() = this@toMyDrawable.minHeight
        }

fun MySprite.toMyDrawable(): MyDrawable =
        object : SpriteDrawable(this), MyDrawable {
            override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float, flipX: Boolean, flipY: Boolean) {
                setFlip(flipX, flipY)
                draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
            }

            override fun update(delta: Float) = this@toMyDrawable.update(delta)
            override fun dispose(manager: TextureManager) = this@toMyDrawable.dispose(manager)

            override fun setPlayingType(type: AnimationType, forceReset: Boolean) =
                    this@toMyDrawable.setPlayingType(type, forceReset)

            override fun getPlayingType() = this@toMyDrawable.playingType
            override fun isAnimationFinished() = this@toMyDrawable.isAnimationFinished()
        }
