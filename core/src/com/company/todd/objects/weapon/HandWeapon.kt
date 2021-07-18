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
        ownerOffset.sub(owner.width / 2, 0f)
            .scl(if (owner.isDirectedToRight) 1f else -1f, 1f)
            .sub(if (owner.isDirectedToRight) 0f else drawableWidth, 0f)
            .add(owner.width / 2, 0f)!!


    override fun postUpdate(delta: Float) {
        listOf(
            Triple(handWeaponStyle.handPosition, handWeaponStyle.handSize, handDrawableActor),
            Triple(handWeaponStyle.weaponPosition, handWeaponStyle.weaponSize, weaponDrawableActor)
        ).forEach { (pos, size, nullableActor) ->
            nullableActor?.let { actor ->
                val newPos = getDrawablePosition(pos.cpy(), size.x)
                actor.setSize(size.x, size.y)
                actor.setPosition(newPos.x, newPos.y)

                val origin = Vector2(
                    handWeaponStyle.origin.x - owner.width / 2, handWeaponStyle.origin.y
                ).scl(if (owner.isDirectedToRight) 1f else -1f, 1f)
                actor.setOrigin(origin.x, origin.y)

                actor.setScale(scaleX, scaleY)
                actor.rotation = rotation
                actor.flipX = !owner.isDirectedToRight
                actor.flipY = false
            }
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
        val handDrawable: MyDrawable?, val weaponDrawable: MyDrawable?,
        @JsonFullSerializable val handPosition: Vector2,
        @JsonFullSerializable val handSize: Vector2,
        @JsonFullSerializable val weaponPosition: Vector2,
        @JsonFullSerializable val weaponSize: Vector2,
        @JsonFullSerializable val origin: Vector2
    ) {
        @JsonFullSerializable
        private val handDrawableName: String?
            get() = handDrawable?.drawableName

        @JsonFullSerializable
        private val weaponDrawableName: String?
            get() = weaponDrawable?.drawableName

        companion object {
            @ManualJsonConstructor
            private fun getJsonDefaults(
                @Suppress("UNUSED_PARAMETER") json: JsonValue,
                parsed: MutableMap<String, Pair<Any?, Boolean>>
            ) {
                JsonDefaults.setDefault("handDrawable", null, parsed)
                JsonDefaults.setDefault("weaponDrawable", null, parsed)
                JsonDefaults.setDefault("handPosition", Vector2(), parsed)
                JsonDefaults.setDefault("weaponPosition", Vector2(), parsed)
                JsonDefaults.setDefault("handSize", Vector2(), parsed)
                JsonDefaults.setDefault("weaponSize", Vector2(), parsed)
                JsonDefaults.setDefault("origin", Vector2(), parsed)
            }
        }
    }
}
