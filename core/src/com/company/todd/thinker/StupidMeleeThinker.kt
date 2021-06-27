package com.company.todd.thinker

import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.SerializationType
import com.company.todd.objects.creature.Creature
import com.company.todd.screen.GameScreen

@SerializationType("stupidMeleeThinker")
class StupidMeleeThinker(
    @JsonFullSerializable private val maxDistanceFromTarget: Float,
    @JsonFullSerializable private val jumpCooldown: Float
) : Thinker {
    private var sinceJump = jumpCooldown

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        sinceJump += delta

        if (operatedObject.canAttack()) {
            operatedObject.attack()
        }

        val myAABB = operatedObject.getAABB()
        val targetAABB = screen.player.getAABB()

        if (targetAABB.x - myAABB.x - myAABB.width > maxDistanceFromTarget) {
            operatedObject.isDirectedToRight = true
            operatedObject.run(true)
        } else if (targetAABB.x + targetAABB.width - myAABB.x < -maxDistanceFromTarget) {
            operatedObject.isDirectedToRight = false
            operatedObject.run(false)
        }

        if (sinceJump >= jumpCooldown && targetAABB.y - myAABB.y > 1f) {
            sinceJump = 0f
            operatedObject.jump()
        }
    }
}
