package com.company.todd.objects.weapon

import com.badlogic.gdx.math.Vector2
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.SerializationType
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.weapon.bullet.Bullet

@SerializationType("weapon", "simpleGun")
open class SimpleGun(
    private val game: ToddGame,
    handWeaponStyle: Style, override val power: Float,
    cooldown: Float, sinceAttackTillDamage: Float,
    @JsonFullSerializable protected val bulletOffset: Vector2,
    @JsonFullSerializable protected val bulletBuilder: Bullet.Builder
) : HandWeapon(handWeaponStyle, cooldown, sinceAttackTillDamage) {
    override fun doAttack() {
        screen.addObject(
            bulletBuilder.build(
                game,
                power,
                getDrawablePosition(
                    listOf(
                        handWeaponStyle.handDrawable?.offset,
                        handWeaponStyle.weaponDrawable?.offset
                    )
                        .mapNotNull { it }
                        .fold(bulletOffset.cpy()) { v1, v2 -> v1.add(v2) },
                    0f  // TODO add width if x flipped
                ).add(owner.x, owner.y),
                if (owner.isDirectedToRight) Vector2(1f, 0f) else Vector2(-1f, 0f),
                owner
            )
        )
    }
}
