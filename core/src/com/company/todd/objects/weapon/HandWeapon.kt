package com.company.todd.objects.weapon

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.company.todd.objects.base.InGameObject
import com.company.todd.screen.game.GameScreen
import com.company.todd.asset.texture.DisposableByManager
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.TextureManager
import com.company.todd.asset.texture.animated.AnimationType
import com.company.todd.json.*

abstract class HandWeapon(
    @JsonFullSerializable private val handWeaponStyle: Style,
    @JsonFullSerializable protected val cooldown: Float,
    @JsonFullSerializable protected val sinceAttackTillDamage: Float
) :
        Weapon(), DisposableByManager {
    protected lateinit var owner: InGameObject
    @JsonUpdateSerializable
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
        originX = handWeaponStyle.origin.x - x
        originY = handWeaponStyle.origin.y - y
    }

    override fun act(delta: Float) {
        super.act(delta)
        sinceAttack += delta
        if (!doneAttack && sinceAttack >= sinceAttackTillDamage) {
            doneAttack = true
            doAttack()
        }

        listOf(handWeaponStyle.handDrawable, handWeaponStyle.weaponDrawable).forEach { drawable ->
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

        val handPos = handWeaponStyle.handPosition.cpy()
        val weaponPos = handWeaponStyle.weaponPosition.cpy()

        val origin = Vector2(originX, originY).scl(if (owner.isDirectedToRight) 1f else -1f, 1f)
        listOf(handPos, weaponPos).forEach {
            it.sub(x, y).scl(if (owner.isDirectedToRight) 1f else -1f, 1f).add(x, y)
        }

        if (!owner.isDirectedToRight) {
            if (handWeaponStyle.handDrawable != null) {
                handPos.sub(handWeaponStyle.handDrawable.minWidth, 0f)
            }
            if (handWeaponStyle.weaponDrawable != null) {
                weaponPos.sub(handWeaponStyle.weaponDrawable.minWidth, 0f)
            }
        }

        handWeaponStyle.handDrawable?.draw(batch, handPos.x, handPos.y, origin.x, origin.y,
                handWeaponStyle.handDrawable.minWidth, handWeaponStyle.handDrawable.minHeight,
                scaleX, scaleY, rotation, !owner.isDirectedToRight, false)

        handWeaponStyle.weaponDrawable?.draw(batch, weaponPos.x, weaponPos.y, origin.x, origin.y,
                handWeaponStyle.weaponDrawable.minWidth, handWeaponStyle.weaponDrawable.minHeight,
                scaleX, scaleY, rotation, !owner.isDirectedToRight, false)

        batch.color = batch.color.apply { a = batchAlpha }
    }

    abstract fun doAttack()

    final override fun attack() {
        if (canAttack()) {
            sinceAttack = 0f
            doneAttack = false
            handWeaponStyle.handDrawable?.setPlayingType(AnimationType.ACTION, true)
            handWeaponStyle.weaponDrawable?.setPlayingType(AnimationType.ACTION, true)
            if (sinceAttackTillDamage == 0f) {
                doAttack()
            }
        }
    }

    final override fun canAttack() = sinceAttack >= cooldown

    override fun dispose(manager: TextureManager) {
        handWeaponStyle.weaponDrawable?.dispose(manager)
        handWeaponStyle.handDrawable?.dispose(manager)
    }

    @SerializationType("handWeaponStyle")
    class Style(
        val handDrawable: MyDrawable?, val weaponDrawable: MyDrawable?,
        @JsonFullSerializable val handPosition: Vector2,
        @JsonFullSerializable val weaponPosition: Vector2,
        @JsonFullSerializable val origin: Vector2
    ) {
        @JsonFullSerializable
        private val handDrawableName: String?
            get() = handDrawable?.drawableName

        @JsonFullSerializable
        private val weaponDrawableName: String?
            get() = weaponDrawable?.drawableName
    }
}
