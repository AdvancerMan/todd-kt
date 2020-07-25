package com.company.todd.util.asset.texture.drawable

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable
import kotlin.math.min

class CoveredTiledDrawable(coverTile: TextureRegion, bodyTile: TextureRegion) : BaseDrawable(), FlipTransformDrawable {
    private val cover = TransformTiledDrawable(coverTile)
    private val body = TransformTiledDrawable(bodyTile)

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        val h = min(cover.minHeight, height)
        cover.draw(batch, x, y + height - h, width, h)
        if (h != height) {
            body.draw(batch, x, y, width, height - h)
        }
    }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) {
        val h = min(cover.minHeight, height)
        cover.draw(batch, x, y + height - h, originX, originY - height + h, width, h, scaleX, scaleY, rotation)
        if (h != height) {
            body.draw(batch, x, y, originX, originY, width, height - h, scaleX, scaleY, rotation)
        }
    }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float, flipX: Boolean, flipY: Boolean) {
        if (flipX || flipY) {
            Gdx.app.error("CoveredTiledDrawable", "Flip is not supported")
        }
        draw(batch, x, y, width, originX, originY, height, scaleX, scaleY, rotation)
    }
}
