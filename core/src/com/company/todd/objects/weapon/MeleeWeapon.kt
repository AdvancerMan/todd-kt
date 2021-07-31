package com.company.todd.objects.weapon

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Fixture
import com.company.todd.json.JsonFullSerializable
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.worldAABBFor
import com.company.todd.screen.game.GameScreen

abstract class MeleeWeapon(
    handWeaponStyle: Style,
    @JsonFullSerializable protected val attackXYWH: Rectangle,
    cooldown: Float, safeAttackPeriod: Float, dangerousAttackPeriod: Float
) : HandWeapon(handWeaponStyle, cooldown, safeAttackPeriod, dangerousAttackPeriod) {
    private val attacked = mutableSetOf<InGameObject>()

    override fun init(owner: InGameObject, screen: GameScreen) {
        super.init(owner, screen)
        attackXYWH.setPosition(attackXYWH.x - x, attackXYWH.y - y)
    }

    override fun doAttack() {
        if (doingFirstHit) {
            attacked.clear()
        }
        worldAABBFor(Rectangle(attackXYWH)).let { aabb ->
            if (!owner.isDirectedToRight) {
                aabb.setPosition(aabb.x - aabb.width - owner.body.getAABB().width, aabb.y)
            }

            screen.queryAABB(aabb.x, aabb.y, aabb.x + aabb.width, aabb.y + aabb.height) {
                val igo = it.body.userData as InGameObject
                // TODO remove attacked set (attack everybody on each frame)
                //  and add invulnerability for creatures
                if (shouldAttack(it) && attacked.add(igo)) {
                    igo.takeDamage(power)
                }
                true
            }
        }
    }

    protected abstract fun shouldAttack(fixture: Fixture): Boolean
}
