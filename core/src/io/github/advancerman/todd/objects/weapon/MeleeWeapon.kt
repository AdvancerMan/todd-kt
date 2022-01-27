package io.github.advancerman.todd.objects.weapon

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Fixture
import io.github.advancerman.todd.json.JsonFullSerializable
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.base.worldAABBFor
import io.github.advancerman.todd.screen.game.GameScreen

abstract class MeleeWeapon(
    handWeaponStyle: Style,
    @JsonFullSerializable protected val attackXYWH: Rectangle,
    cooldown: Float, safeAttackPeriod: Float, dangerousAttackPeriod: Float
) : HandWeapon(handWeaponStyle, cooldown, safeAttackPeriod, dangerousAttackPeriod),
    WithCalculableAttackedObjects {
    private val attacked = mutableSetOf<InGameObject>()

    override fun init(owner: InGameObject, screen: GameScreen) {
        super.init(owner, screen)
        attackXYWH.setPosition(attackXYWH.x - x, attackXYWH.y - y)
    }

    override fun doAttack() {
        if (doingFirstHit) {
            attacked.clear()
        }

        calculateAttackedObjects()
            .filter { !attacked.contains(it) }
            .onEach { it.takeDamage(power) }
            .let { attacked.addAll(it) }
    }

    override fun calculateAttackedObjects(): Set<InGameObject> {
        val result = mutableSetOf<InGameObject>()
        worldAABBFor(Rectangle(attackXYWH)).let { aabb ->
            if (!owner.isDirectedToRight) {
                aabb.setPosition(aabb.x - aabb.width - owner.body.getAABB().width, aabb.y)
            }

            screen.queryAABB(aabb.x, aabb.y, aabb.x + aabb.width, aabb.y + aabb.height) {
                val igo = it.body.userData as InGameObject
                // TODO remove attacked set (attack everybody on each frame)
                //  and add invulnerability for creatures
                if (shouldAttack(it)) {
                    result.add(igo)
                }
                true
            }
        }
        return result
    }

    protected abstract fun shouldAttack(fixture: Fixture): Boolean
}
