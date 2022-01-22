package io.github.advancerman.todd.objects.weapon

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Fixture
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.base.InGameObject

/**
 * Simple melee weapon without modifications
 *
 * @param handWeaponStyle Style of the weapon
 * @param attackXYWH Rectangle relative to unrotated, unflipped owner's position
 *                   in which damage is done
 * @param power Amount of damage done by attack
 * @param cooldown Minimum time period between attack end and next attack beginning
 * @param safeAttackPeriod Time period after attack beginning, when no damage is done
 * @param dangerousAttackPeriod Time period after safe period, when damage is done
 */
@SerializationType([Weapon::class], "SimpleMeleeWeapon")
class SimpleMeleeWeapon(
    handWeaponStyle: Style,
    attackXYWH: Rectangle,
    override var power: Float,
    cooldown: Float,
    safeAttackPeriod: Float,
    dangerousAttackPeriod: Float = safeAttackPeriod
) : MeleeWeapon(handWeaponStyle, attackXYWH, cooldown, safeAttackPeriod, dangerousAttackPeriod) {
    override fun shouldAttack(fixture: Fixture) =
        fixture.body.userData as InGameObject != owner
}
