package com.company.todd.objects.active.creature.enemy

import com.badlogic.gdx.math.Rectangle
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.RectangleCreature
import com.company.todd.objects.active.creature.weapon.HandWeapon
import com.company.todd.objects.active.creature.weapon.SimpleMeleeWeapon
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.asset.texture.MyDrawable

const val stupidEnemyDistanceFromTarget = 3f

// aabb should be at least 2 pix height
class StupidMeleeEnemy(game: ToddGame, drawable: MyDrawable,
                       weaponStyle: HandWeapon.Style, private val target: InGameObject,
                       attackRadius: Float, power: Float, aabb: Rectangle,
                       speed: Float, jumpPower: Float, maxHealth: Float,
                       private val attackCooldown: Float = 1f,
                       private val jumpCooldown: Float = 1.5f) :
        RectangleCreature(
                game, drawable, aabb,
                SimpleMeleeWeapon(
                        weaponStyle,
                        Rectangle(aabb.width, 1f, attackRadius, aabb.height - 2),
                        power
                ), speed, jumpPower, maxHealth
        ) {
    private var sinceAttack = attackCooldown
    private var sinceJump = jumpCooldown

    override fun think(delta: Float) {
        sinceAttack += delta
        sinceJump += delta

        if (sinceAttack >= attackCooldown) {
            sinceAttack = 0f
            attack()
        }

        val myAABB = getAABB()
        val targetAABB = target.getAABB()

        if (targetAABB.x - myAABB.x - myAABB.width > stupidEnemyDistanceFromTarget) {
            isDirectedToRight = true
            run(true)
        } else if (targetAABB.x + targetAABB.width - myAABB.x < -stupidEnemyDistanceFromTarget) {
            isDirectedToRight = false
            run(false)
        }

        if (sinceJump >= jumpCooldown && targetAABB.y - myAABB.y > 1f) {
            sinceJump = 0f
            jump()
        }
    }
}
