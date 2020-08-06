package com.company.todd.objects.active.creature.enemy

import com.badlogic.gdx.math.Rectangle
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.weapon.HandWeapon
import com.company.todd.objects.active.creature.weapon.SimpleMeleeWeapon
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.asset.texture.MyDrawable

// aabb should be at least 2 pix height
class StupidMeleeEnemy(game: ToddGame, drawable: MyDrawable,
                       weaponStyle: HandWeapon.Style, weaponSinceAttackTillDamage: Float,
                       target: InGameObject,
                       attackRadius: Float, power: Float, aabb: Rectangle,
                       speed: Float, jumpPower: Float, maxHealth: Float,
                       attackCooldown: Float = 1f, jumpCooldown: Float = 1.5f) :
        StupidEnemy(
                game, drawable,
                SimpleMeleeWeapon(
                        weaponStyle,
                        Rectangle(aabb.width, 1f, attackRadius, aabb.height - 2),
                        power, attackCooldown, weaponSinceAttackTillDamage
                ),
                target, aabb, speed, jumpPower, maxHealth, jumpCooldown
        )
