package com.company.todd.objects.active.creature.weapon

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.company.todd.objects.base.InGameObject
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.DisposableByManager
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.asset.texture.TextureManager
import com.company.todd.util.asset.texture.animated.AnimationType

const val handWeaponOriginXOffset = 1f

abstract class HandWeapon(private val style: Style, protected val cooldown: Float,
                          protected val sinceAttackTillDamage: Float) :
        Weapon(), DisposableByManager {
    protected lateinit var owner: InGameObject
    protected var sinceAttack = cooldown
    private var doneAttack = true

    override fun init(owner: InGameObject, screen: GameScreen) {
        super.init(owner, screen)
        this.owner = owner
        updatePositionAndOrigin()
    }

    private fun updatePositionAndOrigin() {
        x = owner.width / 2
        y = 0f
        originX = style.handPosition.x + handWeaponOriginXOffset - x
        originY = style.handPosition.y + (style.handDrawable?.minHeight ?: 0f) / 2 - y
    }

    override fun act(delta: Float) {
        super.act(delta)
        sinceAttack += delta
        if (!doneAttack && sinceAttack >= sinceAttackTillDamage) {
            doneAttack = true
            doAttack()
        }

        listOf(style.handDrawable, style.weaponDrawable).forEach { drawable ->
            drawable?.apply {
                update(delta)
                if (getPlayingType() == AnimationType.ACTION && isAnimationFinished()) {
                    setPlayingType(AnimationType.STAY)
                }
            }
        }
        updatePositionAndOrigin()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val batchAlpha = batch.color.a
        batch.color = batch.color.apply { a *= parentAlpha }

        val handPos = style.handPosition.cpy()
        val weaponPos = style.weaponPosition.cpy()

        val origin = Vector2(originX, originY).scl(if (owner.isDirectedToRight) 1f else -1f, 1f)
        listOf(handPos, weaponPos).forEach {
            it.sub(x, y).scl(if (owner.isDirectedToRight) 1f else -1f, 1f).add(x, y)
        }

        if (!owner.isDirectedToRight) {
            if (style.handDrawable != null) {
                handPos.sub(style.handDrawable.minWidth, 0f)
            }
            if (style.weaponDrawable != null) {
                weaponPos.sub(style.weaponDrawable.minWidth, 0f)
            }
        }

        style.handDrawable?.draw(batch, handPos.x, handPos.y, origin.x, origin.y,
                style.handDrawable.minWidth, style.handDrawable.minHeight,
                scaleX, scaleY, rotation, !owner.isDirectedToRight, false)

        style.weaponDrawable?.draw(batch, weaponPos.x, weaponPos.y, origin.x, origin.y,
                style.weaponDrawable.minWidth, style.weaponDrawable.minHeight,
                scaleX, scaleY, rotation, !owner.isDirectedToRight, false)

        batch.color = batch.color.apply { a = batchAlpha }
    }

    abstract fun doAttack()

    final override fun attack() {
        if (canAttack()) {
            sinceAttack = 0f
            doneAttack = false
            style.handDrawable?.setPlayingType(AnimationType.ACTION, true)
            style.weaponDrawable?.setPlayingType(AnimationType.ACTION, true)
            if (sinceAttackTillDamage == 0f) {
                doAttack()
            }
        }
    }

    final override fun canAttack() = sinceAttack >= cooldown

    override fun dispose(manager: TextureManager) {
        style.weaponDrawable?.dispose(manager)
        style.handDrawable?.dispose(manager)
    }

    class Style(val handDrawable: MyDrawable?, val weaponDrawable: MyDrawable?,
                val handPosition: Vector2, val weaponPosition: Vector2)
}
