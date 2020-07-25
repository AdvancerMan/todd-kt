package com.company.todd.util.asset.texture.drawable

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable

class TransformTiledDrawable(region: TextureRegion) : TiledDrawable(region) {
    private val temp: Color = Color()

    /**
     * @see TiledDrawable.draw(Batch, Float, Float, Float, Float)
     */
    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) {
        val batchColor = batch.color
        temp.set(batchColor)
        batch.color = batchColor.mul(color)

        val region = region
        val regionWidth = region.regionWidth.toFloat()
        val regionHeight = region.regionHeight.toFloat()
        val fullX = (width / regionWidth).toInt()
        val fullY = (height / regionHeight).toInt()
        val remainingX = (width - regionWidth * fullX).toInt().toFloat()
        val remainingY = (height - regionHeight * fullY).toInt().toFloat()
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
            region.regionHeight = remainingY.toInt()
            for (i in 0 until fullX) {
                batch.draw(
                        region, x + i * regionWidth, y + endY,
                        originX - i * regionWidth, originY - endY,
                        regionWidth, remainingY, scaleX, scaleY, rotation
                )
            }
        }
        region.regionHeight = regionHeight.toInt()

        batch.color = temp
    }
}
