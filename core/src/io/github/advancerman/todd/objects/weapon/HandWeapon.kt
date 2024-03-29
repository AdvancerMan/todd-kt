package io.github.advancerman.todd.objects.weapon

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.Pools
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.asset.texture.DisposableByManager
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.asset.texture.TextureManager
import io.github.advancerman.todd.asset.texture.animated.AnimationType
import io.github.advancerman.todd.json.*
import io.github.advancerman.todd.objects.base.toDrawableActor
import io.github.advancerman.todd.util.mirrorIf

abstract class HandWeapon(
    @JsonFullSerializable protected val handWeaponStyle: Style,
    @JsonFullSerializable protected val cooldown: Float,
    @JsonFullSerializable protected val safeAttackPeriod: Float,
    @JsonFullSerializable protected val dangerousAttackPeriod: Float
) : Weapon(), DisposableByManager {
    protected lateinit var owner: InGameObject
    protected lateinit var screen: GameScreen
    private val handDrawableActor = handWeaponStyle.handDrawable?.toDrawableActor()
    private val weaponDrawableActor = handWeaponStyle.weaponDrawable?.toDrawableActor()

    @JsonUpdateSerializable
    protected var sinceAttack = cooldown
    protected var doingFirstHit = false

    override fun init(owner: InGameObject, screen: GameScreen) {
        super.init(owner, screen)
        this.owner = owner
        this.screen = screen
        handDrawableActor?.let { owner.addActor(it) }
        weaponDrawableActor?.let { owner.addActor(it) }
    }

    override fun act(delta: Float) {
        sinceAttack += delta
        super.act(delta)
        if (safeAttackPeriod <= sinceAttack && (sinceAttack <= dangerousAttackPeriod || doingFirstHit)) {
            doAttack()
            doingFirstHit = false
        }
    }

    protected fun getDrawablePosition(ownerOffset: Vector2, drawableWidth: Float) =
        ownerOffset.mirrorIf(!owner.isDirectedToRight, owner.width / 2, -drawableWidth)

    override fun postUpdate(delta: Float) {
        val handPos = handDrawableActor?.let { it.drawable!!.offset.cpy() } ?: Vector2()
        val weaponPos = weaponDrawableActor?.let { it.drawable!!.offset.cpy().add(handPos) }
        listOf(handPos to handDrawableActor, weaponPos to weaponDrawableActor)
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
            doingFirstHit = true
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

    /**
     * Actor description for HandWeapon
     * @param handDrawable Drawable for hand. Z-index is relative to owner's actor,
     *                     offset is used for drawable positioning relative to owner's position.
     * @param weaponDrawable Drawable for weapon. Z-index is relative to owner's actor,
     *                       offset is used for drawable positioning relative to hand's position.
     * @param origin Coordinate system origin for rotation and scale
     */
    @SerializationType([Style::class])
    data class Style(
        @JsonFullSerializable val handDrawable: ToddDrawable? = null,
        @JsonFullSerializable val weaponDrawable: ToddDrawable? = null,
        @JsonFullSerializable val origin: Vector2 = Vector2(0f, 0f)
    )
}
