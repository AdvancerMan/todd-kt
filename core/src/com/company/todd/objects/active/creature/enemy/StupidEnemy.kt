package com.company.todd.objects.active.creature.enemy

import com.badlogic.gdx.math.Rectangle
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.RectangleCreature
import com.company.todd.objects.active.creature.weapon.Weapon
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.asset.texture.MyDrawable

const val stupidEnemyDistanceFromTarget = 3f

open class StupidEnemy(game: ToddGame, drawable: MyDrawable, weapon: Weapon?,
                  private val target: InGameObject, aabb: Rectangle,
                  speed: Float, jumpPower: Float, maxHealth: Float,
                  private val jumpCooldown: Float = 1.5f) :
        RectangleCreature(game, drawable, aabb, weapon, speed, jumpPower, maxHealth) {
    private var sinceJump = jumpCooldown

    override fun think(delta: Float) {
        sinceJump += delta

        if (weapon!!.canAttack()) {
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
