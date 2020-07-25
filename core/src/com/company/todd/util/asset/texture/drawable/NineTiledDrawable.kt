package com.company.todd.util.asset.texture.drawable

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable
import kotlin.math.max

class NineTiledDrawable(region: TextureRegion, lw: Int, rw: Int, uh: Int, dh: Int) :
        BaseDrawable(), TransformDrawable {
    private val luCorner = TextureRegion(region, 0, 0, lw, uh)
    private val ruCorner = TextureRegion(region, region.regionWidth - rw, 0, rw, uh)
    private val rdCorner = TextureRegion(region, region.regionWidth - rw, region.regionHeight - dh, rw, dh)
    private val ldCorner = TextureRegion(region, 0, region.regionHeight - dh, lw, dh)

    private val lTile = TiledDrawable(TextureRegion(
            region, 0, uh, lw, region.regionHeight - uh - dh
    ))
    private val uTile = TiledDrawable(TextureRegion(
            region, lw, 0, region.regionWidth - lw - rw, uh
    ))
    private val rTile = TiledDrawable(TextureRegion(
            region, region.regionWidth - rw, uh, rw, region.regionHeight - uh - dh
    ))
    private val dTile = TiledDrawable(TextureRegion(
            region, lw, region.regionHeight - dh, region.regionWidth - lw - rw, dh
    ))
    private val mTile = TiledDrawable(TextureRegion(
            region, lw, uh, region.regionWidth - lw - rw, region.regionHeight - uh - dh
    ))

    override fun draw(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        draw(batch, x, y, 0f, 0f, width, height, 1f, 1f, 0f)
    }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float) {
        val lx = x
        val mx = x + ldCorner.regionWidth
        val rx = x + width - rdCorner.regionWidth

        val dy = y
        val my = y + ldCorner.regionHeight
        val uy = y + height - luCorner.regionHeight

        val lw = ldCorner.regionWidth.toFloat()
        val rw = rdCorner.regionWidth.toFloat()
        val mw = max(0f, width - lw - rw)

        val dh = ldCorner.regionHeight.toFloat()
        val uh = luCorner.regionHeight.toFloat()
        val mh = max(0f, height - dh - uh)

        batch.draw(ldCorner, lx, dy, originX, originY, lw, dh, scaleX, scaleY, rotation)
        batch.draw(luCorner, lx, uy, originX, originY, lw, uh, scaleX, scaleY, rotation)
        batch.draw(ruCorner, rx, uy, originX, originY, rw, uh, scaleX, scaleY, rotation)
        batch.draw(rdCorner, rx, dy, originX, originY, rw, dh, scaleX, scaleY, rotation)
        // TODO MyTiledDrawable to draw rotated/scaled tiles
        lTile.draw(batch, lx, my /*, originX, originY */, lw, mh /*, scaleX, scaleY, rotation */)
        uTile.draw(batch, mx, uy /*, originX, originY */, mw, uh /*, scaleX, scaleY, rotation */)
        rTile.draw(batch, rx, my /*, originX, originY */, rw, mh /*, scaleX, scaleY, rotation */)
        dTile.draw(batch, mx, dy /*, originX, originY */, mw, dh /*, scaleX, scaleY, rotation */)
        mTile.draw(batch, mx, my /*, originX, originY */, mw, mh /*, scaleX, scaleY, rotation */)
    }
}
