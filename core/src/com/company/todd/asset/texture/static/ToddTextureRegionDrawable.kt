package com.company.todd.asset.texture.static

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.company.todd.asset.texture.BaseToddDrawable
import com.company.todd.asset.texture.ToddDrawable
import com.company.todd.asset.texture.RegionInfo
import com.company.todd.asset.texture.TextureManager

class ToddTextureRegionDrawable(
    private val info: RegionInfo?,
    private val region: TextureRegion
) : BaseToddDrawable(), ToddDrawable {
    init {
        setMinSize(region.regionWidth.toFloat(), region.regionHeight.toFloat())
    }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float, flipX: Boolean, flipY: Boolean) {
        batch.draw(
                region.texture, x, y, originX, originY, width, height,
                scaleX, scaleY, rotation, region.regionX, region.regionY,
                region.regionWidth, region.regionHeight, flipX, flipY
        )
    }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) {
        batch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
    }

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        batch.draw(region, x, y, width, height)
    }

    override fun dispose(manager: TextureManager) {
        info?.let { manager.unload(it) }
    }
}
