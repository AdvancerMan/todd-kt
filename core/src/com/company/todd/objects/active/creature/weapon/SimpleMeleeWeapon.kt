package com.company.todd.objects.active.creature.weapon

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Fixture

class SimpleMeleeWeapon(weaponStyle: Style, attackAABB: Rectangle, powerAmount: Float) :
        MeleeWeapon(weaponStyle, attackAABB) {
    override var power = powerAmount
    override fun shouldAttack(fixture: Fixture) = true
}
