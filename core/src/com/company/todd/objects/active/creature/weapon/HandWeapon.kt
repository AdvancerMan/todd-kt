package com.company.todd.objects.active.creature.weapon

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.company.todd.util.asset.texture.MyDrawable

abstract class HandWeapon(private val weaponDrawable: MyDrawable?, private val handDrawable: MyDrawable?,
                          weaponPosition: Vector2?, handPosition: Vector2?) : Weapon() {
    init {
        x = handPosition?.x ?: 0f
        y = handPosition?.y ?: 0f
        width = (weaponPosition?.x ?: x) - x
        height = (weaponPosition?.y ?: y) - y
        originX = x
        originY = y + height / 2
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
}
