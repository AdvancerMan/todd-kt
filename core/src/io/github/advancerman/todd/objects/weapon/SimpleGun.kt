package io.github.advancerman.todd.objects.weapon

import com.badlogic.gdx.math.Vector2
import io.github.advancerman.todd.json.JsonFullSerializable
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.weapon.bullet.Bullet

/**
 * Simple gun without modifications
 *
 *
 * @param handWeaponStyle Style of the weapon
 * @param power Amount of damage done by attack
 * @param cooldown Minimum time period between attack end and next attack beginning
 * @param safeAttackPeriod Time period after attack beginning till actual shot
 * @param bulletOffset Bullet spawn position relative to unrotated, unflipped hand's position
 * @param bulletBuilder Would be replaced by bullet json pattern in future releases
 */
@SerializationType(Weapon::class, "SimpleGun")
open class SimpleGun(
    private val game: ToddGame,
    handWeaponStyle: Style, override val power: Float,
    cooldown: Float, safeAttackPeriod: Float,
    @JsonFullSerializable protected val bulletOffset: Vector2,
    @JsonFullSerializable protected val bulletBuilder: Bullet.Builder
) : HandWeapon(handWeaponStyle, cooldown, safeAttackPeriod, safeAttackPeriod) {
    override fun doAttack() {
        screen.addObject(
            bulletBuilder.build(
                game,
                power,
                localToStageCoordinates(
                    getDrawablePosition(
                        listOf(handWeaponStyle.handDrawable, handWeaponStyle.weaponDrawable)
                            .mapNotNull { it?.offset }
                            .fold(bulletOffset.cpy()) { v1, v2 -> v1.add(v2) },
                        0f  // TODO add width if x flipped
                    )
                ),
                if (owner.isDirectedToRight) Vector2(1f, 0f) else Vector2(-1f, 0f),
                owner
            )
        )
    }
}
