package com.company.todd.objects.weapon

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Fixture
import com.company.todd.json.JsonFullSerializable
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.worldAABBFor
import com.company.todd.screen.game.GameScreen

abstract class MeleeWeapon(
    handWeaponStyle: Style,
    @JsonFullSerializable("attackXYWH") protected val attackAABB: Rectangle,
    cooldown: Float, sinceAttackTillDamage: Float
) :
        HandWeapon(handWeaponStyle, cooldown, sinceAttackTillDamage) {
    override fun init(owner: InGameObject, screen: GameScreen) {
        super.init(owner, screen)
        attackAABB.setPosition(attackAABB.x - x, attackAABB.y - y)
    }

    override fun doAttack() {
        val attacked = mutableSetOf<InGameObject>()
        worldAABBFor(Rectangle(attackAABB)).let { aabb ->
            if (!owner.isDirectedToRight) {
                aabb.setPosition(aabb.x - aabb.width - owner.getAABB().width, aabb.y)
            }

            screen.queryAABB(aabb.x, aabb.y, aabb.x + aabb.width, aabb.y + aabb.height) {
                if (shouldAttack(it)) {
                    attacked.add(it.body.userData as InGameObject)
                }
                true
            }
        }
        attacked.forEach { if (it != owner) it.takeDamage(power) }
    }

    protected abstract fun shouldAttack(fixture: Fixture): Boolean
}
