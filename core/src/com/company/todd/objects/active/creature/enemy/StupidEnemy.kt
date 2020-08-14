package com.company.todd.objects.active.creature.enemy

import com.badlogic.gdx.math.Vector2
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.RectangleCreature
import com.company.todd.objects.active.creature.weapon.Weapon
import com.company.todd.asset.texture.MyDrawable

open class StupidEnemy(game: ToddGame, drawable: MyDrawable,
                       drawableSize: Vector2, bodyLowerLeftCornerOffset: Vector2,
                       bodyPosition: Vector2, bodySize: Vector2, weapon: Weapon?, speed: Float,
                       jumpPower: Float, maxHealth: Float,
                       private val jumpCooldown: Float, private val maxDistanceFromTarget: Float) :
        RectangleCreature(game, drawable, drawableSize, bodyLowerLeftCornerOffset,
                bodyPosition, bodySize, weapon, speed, jumpPower, maxHealth) {
    private var sinceJump = jumpCooldown

    override fun think(delta: Float) {
        sinceJump += delta

        if (weapon!!.canAttack()) {
            attack()
        }

        val myAABB = getAABB()
        val targetAABB = screen.player.getAABB()

        if (targetAABB.x - myAABB.x - myAABB.width > maxDistanceFromTarget) {
            isDirectedToRight = true
            run(true)
        } else if (targetAABB.x + targetAABB.width - myAABB.x < -maxDistanceFromTarget) {
            isDirectedToRight = false
            run(false)
        }

        if (sinceJump >= jumpCooldown && targetAABB.y - myAABB.y > 1f) {
            sinceJump = 0f
            jump()
        }
    }
}
