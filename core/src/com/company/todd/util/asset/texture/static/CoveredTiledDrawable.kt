package com.company.todd.util.asset.texture.static

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.company.todd.util.asset.texture.CoveredTiledRegionInfo
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.asset.texture.TextureManager
import kotlin.math.min

class CoveredTiledDrawable(private val info: CoveredTiledRegionInfo,
                           coverTile: TextureRegion, bodyTile: TextureRegion) :
        BaseDrawable(), MyDrawable {
    constructor(info: CoveredTiledRegionInfo, region: TextureRegion) :
            this(
                    info,
                    TextureRegion(region, 0, region.regionHeight - info.uh, region.regionWidth, info.uh),
                    region.apply { regionHeight -= info.uh }
            )

    private val cover = TransformTiledDrawable(null, coverTile)
    private val body = TransformTiledDrawable(null, bodyTile)

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
        draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
    }

    override fun dispose(manager: TextureManager) {
        manager.unload(info)
    }
}
