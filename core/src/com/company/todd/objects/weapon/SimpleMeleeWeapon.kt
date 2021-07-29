package com.company.todd.objects.weapon

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Fixture
import com.company.todd.json.SerializationType
import com.company.todd.objects.base.InGameObject

@SerializationType(Weapon::class, "simpleMeleeWeapon")
class SimpleMeleeWeapon(
    handWeaponStyle: Style, attackAABB: Rectangle, override var power: Float,
    cooldown: Float, safeAttackPeriod: Float, dangerousAttackPeriod: Float
) : MeleeWeapon(handWeaponStyle, attackAABB, cooldown, safeAttackPeriod, dangerousAttackPeriod) {
    override fun shouldAttack(fixture: Fixture) =
        fixture.body.userData as InGameObject != owner
}
