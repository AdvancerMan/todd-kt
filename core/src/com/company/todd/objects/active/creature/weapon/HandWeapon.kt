package com.company.todd.objects.active.creature.weapon

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.company.todd.util.asset.texture.DisposableByManager
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.asset.texture.TextureManager
import com.company.todd.util.asset.texture.animated.AnimationType

abstract class HandWeapon(private val weaponDrawable: MyDrawable?, private val handDrawable: MyDrawable?,
                          weaponPosition: Vector2?, handPosition: Vector2?) : Weapon(), DisposableByManager {
    init {
        x = handPosition?.x ?: 0f
        y = handPosition?.y ?: 0f
        width = (weaponPosition?.x ?: x) - x
        height = (weaponPosition?.y ?: y) - y
        originX = x
        originY = y + height / 2
    }

    override fun act(delta: Float) {
        super.act(delta)
        listOf(handDrawable, weaponDrawable).forEach { drawable ->
            drawable?.apply {
                update(delta)
                if (getPlayingType() == AnimationType.ACTION && isAnimationFinished()) {
                    setPlayingType(AnimationType.STAY)
                }
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val batchAlpha = batch.color.a
        batch.color = batch.color.apply { a *= parentAlpha }
        handDrawable?.draw(batch, x, y, originX, originY,
                handDrawable.minWidth, handDrawable.minHeight, scaleX, scaleY, rotation)
        weaponDrawable?.draw(batch, x + width, y + height, originX, originY,
                weaponDrawable.minWidth, weaponDrawable.minHeight, scaleX, scaleY, rotation)
        batch.color = batch.color.apply { a = batchAlpha }
    }

    override fun attack() {
        handDrawable?.setPlayingType(AnimationType.ACTION, true)
        weaponDrawable?.setPlayingType(AnimationType.ACTION, true)
    }

    override fun dispose(manager: TextureManager) {
        handDrawable?.dispose(manager)
        weaponDrawable?.dispose(manager)
    }
}
