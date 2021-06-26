package com.company.todd.objects.weapon

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Fixture

class SimpleMeleeWeapon(weaponStyle: Style, attackAABB: Rectangle, override var power: Float,
                        cooldown: Float, sinceAttackTillDamage: Float) :
        MeleeWeapon(weaponStyle, attackAABB, cooldown, sinceAttackTillDamage) {
    override fun shouldAttack(fixture: Fixture) = true
}
