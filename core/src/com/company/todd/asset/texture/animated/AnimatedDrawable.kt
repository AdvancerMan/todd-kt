package com.company.todd.asset.texture.animated

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.company.todd.asset.texture.BaseMyDrawable
import com.company.todd.asset.texture.MyDrawable

enum class AnimationType {
    STAY, RUN, JUMP, PRE_FALL, FALL, FALL_AFTER_GROUND, ACTION
}

abstract class AnimatedDrawable(playingNow: Animation<MyDrawable>) : BaseMyDrawable() {
    protected var elapsed = 0f
    protected var frame = playingNow.getKeyFrame(0f)!!
    protected var playingNow = playingNow
        set(value) {
            elapsed = 0f
            frame = value.getKeyFrame(0f)
            field = value
        }

    override fun update(delta: Float) {
        super.update(delta)
        elapsed += delta
        frame = playingNow.getKeyFrame(elapsed)
    }

    override fun isAnimationFinished() = playingNow.isAnimationFinished(elapsed)

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float, flipX: Boolean, flipY: Boolean) {
        frame.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation, flipX, flipY)
    }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) {
        frame.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        frame.draw(batch, x, y, width, height)
    }

    override fun getLeftWidth() = frame.leftWidth
    override fun getRightWidth() = frame.rightWidth
    override fun getTopHeight() = frame.topHeight
    override fun getBottomHeight() = frame.bottomHeight
    override fun getMinWidth() = frame.minWidth
    override fun getMinHeight() = frame.minHeight

    open fun forEachFrame(f: (MyDrawable) -> Unit) {
        playingNow.keyFrames.forEach(f)
    }

    override fun setLeftWidth(leftWidth: Float) {
        forEachFrame { it.leftWidth = leftWidth }
    }

    override fun setRightWidth(rightWidth: Float) {
        forEachFrame { it.rightWidth = rightWidth }
    }

    override fun setTopHeight(topHeight: Float) {
        forEachFrame { it.topHeight = topHeight }
    }

    override fun setBottomHeight(bottomHeight: Float) {
        forEachFrame { it.bottomHeight = bottomHeight }
    }

    override fun setMinWidth(minWidth: Float) {
        forEachFrame { it.minWidth = minWidth }
    }

    override fun setMinHeight(minHeight: Float) {
        forEachFrame { it.minHeight = minHeight }
    }
}
