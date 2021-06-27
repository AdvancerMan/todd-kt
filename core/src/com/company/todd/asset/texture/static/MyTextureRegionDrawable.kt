package com.company.todd.asset.texture.static

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.RegionInfo
import com.company.todd.asset.texture.TextureManager

open class MyTextureRegionDrawable(private val info: RegionInfo?, region: TextureRegion) :
        TextureRegionDrawable(region), MyDrawable {
    override var drawableName: String? = null

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float, flipX: Boolean, flipY: Boolean) {
        batch.draw(
                region.texture, x, y, originX, originY, width, height,
                scaleX, scaleY, rotation, region.regionX, region.regionY,
                region.regionWidth, region.regionHeight, flipX, flipY
        )
    }

    override fun dispose(manager: TextureManager) {
        info?.let { manager.unload(it) }
    }
}
