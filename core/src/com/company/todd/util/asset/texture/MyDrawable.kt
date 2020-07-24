package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable

interface MyDrawableI {
    fun update(delta: Float)
    fun dispose(manager: TextureManager)

    fun isDirectedToRight(): Boolean
    fun setDirectedToRight(directedToRight: Boolean)

    // for animations
    fun setPlayingType(type: AnimationType, forceReset: Boolean = false)
    fun getPlayingType(): AnimationType
    fun isAnimationFinished() = true
}

interface MyDrawable : TransformDrawable, MyDrawableI

fun TransformDrawable.toMyDrawable(dispose: (TextureManager) -> Unit, update: (Float) -> Unit = {}) =
        object : MyDrawable {
            override fun dispose(manager: TextureManager) = dispose(manager)
            override fun update(delta: Float) = update(delta)

            override fun setPlayingType(type: AnimationType, forceReset: Boolean) {}
            override fun getPlayingType() = AnimationType.STAY

            override fun isDirectedToRight(): Boolean {
                TODO("not implemented")
            }

            override fun setDirectedToRight(directedToRight: Boolean) {
                TODO("not implemented")
            }

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
            override fun update(delta: Float) = this@toMyDrawable.update(delta)
            override fun dispose(manager: TextureManager) = this@toMyDrawable.dispose(manager)

            override fun isDirectedToRight() = this@toMyDrawable.isDirectedToRight

            override fun setDirectedToRight(directedToRight: Boolean) {
                this@toMyDrawable.isDirectedToRight = directedToRight
            }

            override fun setPlayingType(type: AnimationType, forceReset: Boolean) =
                    this@toMyDrawable.setPlayingType(type, forceReset)

            override fun getPlayingType() = this@toMyDrawable.playingType
            override fun isAnimationFinished() = this@toMyDrawable.isAnimationFinished()
        }
