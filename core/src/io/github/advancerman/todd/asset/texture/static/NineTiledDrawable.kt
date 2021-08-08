package io.github.advancerman.todd.asset.texture.static

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import io.github.advancerman.todd.asset.texture.*
import kotlin.math.max

class NineTiledDrawable(
    private val info: NineTiledRegionInfo?, region: TextureRegion,
    lw: Int, rw: Int, uh: Int, dh: Int
) : BaseToddDrawable() {
    constructor(info: NineTiledRegionInfo, region: TextureRegion) :
            this(info, region, info.lw, info.rw, info.uh, info.dh)

    private val luCorner = TextureRegion(region, 0, 0, lw, uh)
    private val ruCorner = TextureRegion(region, region.regionWidth - rw, 0, rw, uh)
    private val rdCorner = TextureRegion(region, region.regionWidth - rw, region.regionHeight - dh, rw, dh)
    private val ldCorner = TextureRegion(region, 0, region.regionHeight - dh, lw, dh)

    private val lTile = TransformTiledDrawable(null, TextureRegion(
            region, 0, uh, lw, region.regionHeight - uh - dh
    ))
    private val uTile = TransformTiledDrawable(null, TextureRegion(
            region, lw, 0, region.regionWidth - lw - rw, uh
    ))
    private val rTile = TransformTiledDrawable(null, TextureRegion(
            region, region.regionWidth - rw, uh, rw, region.regionHeight - uh - dh
    ))
    private val dTile = TransformTiledDrawable(null, TextureRegion(
            region, lw, region.regionHeight - dh, region.regionWidth - lw - rw, dh
    ))
    private val mTile = TransformTiledDrawable(null, TextureRegion(
            region, lw, uh, region.regionWidth - lw - rw, region.regionHeight - uh - dh
    ))

    init {
        minWidth = lTile.minWidth + mTile.minWidth + rTile.minWidth
        minHeight = uTile.minHeight + mTile.minHeight + dTile.minHeight
        leftWidth = lTile.minWidth
        rightWidth = rTile.minWidth
        topHeight = uTile.minHeight
        bottomHeight = dTile.minHeight
    }

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

        batch.draw(ldCorner, lx, dy, originX + x - lx, originY + y - dy, lw, dh, scaleX, scaleY, rotation)
        batch.draw(luCorner, lx, uy, originX + x - lx, originY + y - uy, lw, uh, scaleX, scaleY, rotation)
        batch.draw(ruCorner, rx, uy, originX + x - rx, originY + y - uy, rw, uh, scaleX, scaleY, rotation)
        batch.draw(rdCorner, rx, dy, originX + x - rx, originY + y - dy, rw, dh, scaleX, scaleY, rotation)
        lTile.draw(batch, lx, my, originX + x - lx, originY + y - my, lw, mh, scaleX, scaleY, rotation)
        uTile.draw(batch, mx, uy, originX + x - mx, originY + y - uy, mw, uh, scaleX, scaleY, rotation)
        rTile.draw(batch, rx, my, originX + x - rx, originY + y - my, rw, mh, scaleX, scaleY, rotation)
        dTile.draw(batch, mx, dy, originX + x - mx, originY + y - dy, mw, dh, scaleX, scaleY, rotation)
        mTile.draw(batch, mx, my, originX + x - mx, originY + y - my, mw, mh, scaleX, scaleY, rotation)
    }

    override fun draw(batch: Batch, x: Float, y: Float, originX: Float, originY: Float, width: Float, height: Float, scaleX: Float, scaleY: Float, rotation: Float, flipX: Boolean, flipY: Boolean) {
        if (flipX || flipY) {
            Gdx.app.error("NineTiledDrawable", "Flip is not supported")
        }
        draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
    }

    override fun dispose(manager: TextureManager) {
        info?.let { manager.unload(it) }
    }
}
