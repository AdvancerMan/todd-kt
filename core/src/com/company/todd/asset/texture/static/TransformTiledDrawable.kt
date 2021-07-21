package com.company.todd.asset.texture.static

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.TextureManager
import com.company.todd.asset.texture.TiledRegionInfo
import kotlin.math.ceil

class TransformTiledDrawable(private val info: TiledRegionInfo?, region: TextureRegion) : TiledDrawable(region), MyDrawable {
    override var drawableName: String? = null
    override var myZIndex: Int = 0
    override val size = Vector2()
    override val offset = Vector2()

    private val temp: Color = Color()

    /**
     * @see TiledDrawable.draw(Batch, Float, Float, Float, Float)
     */
    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) {
        val batchColor = batch.color
        temp.set(batchColor)
        batch.color = batchColor.mul(color)

        val region = region
        val regionY = region.regionY
        val regionWidth = region.regionWidth.toFloat()
        val regionHeight = region.regionHeight.toFloat()
        val fullX = (width / regionWidth).toInt()
        val fullY = (height / regionHeight).toInt()
        val remainingX = ceil(width - regionWidth * fullX)
        val remainingY = ceil(height - regionHeight * fullY)
        val endX = fullX * regionWidth
        val endY = fullY * regionHeight
        for (i in 0 until fullX) {
            for (j in 0 until fullY) {
                batch.draw(
                        region, x + i * regionWidth, y + j * regionHeight,
                        originX - i * regionWidth, originY - j * regionHeight,
                        regionWidth, regionHeight, scaleX, scaleY, rotation
                )
            }
        }

        if (remainingX > 0) {
            region.regionWidth = remainingX.toInt()
            for (i in 0 until fullY) {
                batch.draw(
                        region, x + endX, y + i * regionHeight,
                        originX - endX, originY - i * regionHeight,
                        remainingX, regionHeight, scaleX, scaleY, rotation
                )
            }

            if (remainingY > 0) {
                region.regionY = regionY + (regionHeight - remainingY).toInt()
                region.regionHeight = remainingY.toInt()
                batch.draw(
                        region, x + endX, y + endY,
                        originX - endX, originY - endY,
                        remainingX, remainingY, scaleX, scaleY, rotation
                )
            }
        }

        region.regionWidth = regionWidth.toInt()

        if (remainingY > 0) {
            region.regionY = regionY + (regionHeight - remainingY).toInt()
            region.regionHeight = remainingY.toInt()
            for (i in 0 until fullX) {
                batch.draw(
                        region, x + i * regionWidth, y + endY,
                        originX - i * regionWidth, originY - endY,
                        regionWidth, remainingY, scaleX, scaleY, rotation
                )
            }
        }
        region.regionY = regionY
        region.regionHeight = regionHeight.toInt()

        batch.color = temp
    }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float, flipX: Boolean, flipY: Boolean) {
        region.flip(region.isFlipX != flipX, region.isFlipY != flipY)
        draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        region.flip(region.isFlipX, region.isFlipY)
    }

    override fun dispose(manager: TextureManager) {
        info?.let { manager.unload(it) }
    }
}
