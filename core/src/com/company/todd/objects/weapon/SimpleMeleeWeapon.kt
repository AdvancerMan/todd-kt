package com.company.todd.objects.weapon

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Fixture
import com.company.todd.json.SerializationType

@SerializationType("weapon", "simpleMeleeWeapon")
class SimpleMeleeWeapon(handWeaponStyle: Style, attackAABB: Rectangle, override var power: Float,
                        cooldown: Float, sinceAttackTillDamage: Float) :
        MeleeWeapon(handWeaponStyle, attackAABB, cooldown, sinceAttackTillDamage) {
    override fun shouldAttack(fixture: Fixture) = true
}
