package com.company.todd.objects.weapon

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.Pools
import com.company.todd.objects.base.InGameObject
import com.company.todd.screen.game.GameScreen
import com.company.todd.asset.texture.DisposableByManager
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.TextureManager
import com.company.todd.asset.texture.animated.AnimationType
import com.company.todd.json.*
import com.company.todd.objects.base.toDrawableActor
import com.company.todd.util.mirrorIf

abstract class HandWeapon(
    @JsonFullSerializable protected val handWeaponStyle: Style,
    @JsonFullSerializable protected val cooldown: Float,
    @JsonFullSerializable protected val sinceAttackTillDamage: Float
) : Weapon(), DisposableByManager {
    protected lateinit var owner: InGameObject
    protected lateinit var screen: GameScreen
    private val handDrawableActor = handWeaponStyle.handDrawable?.toDrawableActor()
    private val weaponDrawableActor = handWeaponStyle.weaponDrawable?.toDrawableActor()

    @JsonUpdateSerializable
    protected var sinceAttack = cooldown
    private var doneAttack = true

    override fun init(owner: InGameObject, screen: GameScreen) {
        super.init(owner, screen)
        this.owner = owner
        this.screen = screen
        handDrawableActor?.let { owner.addActor(it) }
        weaponDrawableActor?.let { owner.addActor(it) }
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
                if (getPlayingType() == AnimationType.ACTION && isAnimationFinished()) {
                    setPlayingType(AnimationType.STAY)
                }
            }
        }
    }

    protected fun getDrawablePosition(ownerOffset: Vector2, drawableWidth: Float) =
        ownerOffset.mirrorIf(!owner.isDirectedToRight, owner.width / 2, -drawableWidth)

    override fun postUpdate(delta: Float) {
        val handPos = handDrawableActor?.drawable?.offset ?: Vector2()
        val weaponPos = weaponDrawableActor?.let {
            it.drawable!!.offset.cpy().add(handPos)
        }
        listOf(handPos to handDrawableActor,  weaponPos to weaponDrawableActor)
            .filter { it.second != null }
            .forEach { (pos, actor) ->
                val size = actor!!.drawable!!.size
                val newPos = getDrawablePosition(pos!!, size.x)
                actor.setSize(size.x, size.y)
                actor.setPosition(newPos.x, newPos.y)

                val origin = handWeaponStyle.origin.cpy()
                    .mirrorIf(!owner.isDirectedToRight, owner.width / 2)
                actor.setOrigin(origin.x, origin.y)

                actor.setScale(scaleX, scaleY)
                actor.rotation = rotation
                actor.flipX = !owner.isDirectedToRight
                actor.flipY = false
            }
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
        listOf(handDrawableActor, weaponDrawableActor).forEach { actor ->
            actor?.let {
                it.drawable!!.dispose(manager)
                it.drawable = null
                owner.removeActor(it)
                Pools.free(it)
            }
        }
    }

    @SerializationType("handWeaponStyle")
    class Style(
        @JsonFullSerializable val handDrawable: MyDrawable?,
        @JsonFullSerializable val weaponDrawable: MyDrawable?,
        @JsonFullSerializable val origin: Vector2
    ) {
        companion object {
            @ManualJsonConstructor
            private fun getJsonDefaults(
                @Suppress("UNUSED_PARAMETER") json: JsonValue,
                parsed: MutableMap<String, Pair<Any?, Boolean>>
            ) {
                JsonDefaults.setDefault("handDrawable", null, parsed)
                JsonDefaults.setDefault("weaponDrawable", null, parsed)
                JsonDefaults.setDefault("origin", Vector2(), parsed)
            }
        }
    }
}
