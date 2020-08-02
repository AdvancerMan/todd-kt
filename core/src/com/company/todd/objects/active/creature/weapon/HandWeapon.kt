package com.company.todd.objects.active.creature.weapon

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.company.todd.util.asset.texture.DisposableByManager
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.asset.texture.TextureManager
import com.company.todd.util.asset.texture.animated.AnimationType

abstract class HandWeapon(style: Style) : Weapon(), DisposableByManager {
    private val weaponDrawable: MyDrawable? = style.weaponDrawable
    private val handDrawable: MyDrawable? = style.handDrawable

    init {
        setHandWeaponPosition(style.weaponPosition, style.handPosition)
    }

    fun setHandWeaponPosition(weaponPosition: Vector2? = null, handPosition: Vector2? = null) {
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

    class Style {
        var weaponDrawable: MyDrawable?
        var handDrawable: MyDrawable?
        var weaponPosition: Vector2?
        var handPosition: Vector2?

        constructor(weaponDrawable: MyDrawable, handDrawable: MyDrawable,
                    weaponPosition: Vector2, handPosition: Vector2) {
            this.weaponDrawable = weaponDrawable
            this.handDrawable = handDrawable
            this.weaponPosition = weaponPosition
            this.handPosition = handPosition
        }

        constructor(weaponDrawable: MyDrawable, weaponPosition: Vector2) {
            this.weaponDrawable = weaponDrawable
            this.weaponPosition = weaponPosition
            handDrawable = null
            handPosition = null
        }
    }
}
