package com.company.todd.util.asset.texture.drawable

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable
import kotlin.math.min

class CoveredTiledDrawable(coverTile: TextureRegion, bodyTile: TextureRegion) : BaseDrawable(), TransformDrawable {
    private val cover = TiledDrawable(coverTile)
    private val body = TiledDrawable(bodyTile)

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        val h = min(cover.minHeight, height)
        cover.draw(batch, x, y + height - h, width, h)
        if (h != height) {
            body.draw(batch, x, y, width, height - h)
        }
    }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) {
        // TODO MyTiledDrawable to draw rotated/scaled tiles
        val h = min(cover.minHeight, height)
        cover.draw(batch, x, y + height - h, originX, originX, width, h, scaleX, scaleY, rotation)
        if (h != height) {
            body.draw(batch, x, y, width, originX, originX, height - h, scaleX, scaleY, rotation)
        }
    }
}
