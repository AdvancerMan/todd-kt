package io.github.advancerman.todd.thinker.jump

import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.JumpAction
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.thinker.Thinker

/**
 * Thinker that triggers jump every frame with cooldown if target is higher than operated object.
 *
 * ## Behaviour
 *
 * On each frame it checks if last jump was performed at least [jumpCooldown] seconds ago.
 * Then checks if target object is at least [highGroundThreshold] pixels higher than operated object.
 * Then checks if operated object is on ground.
 * If check succeeds operated object will perform jump.
 *
 */
@SerializationType([Thinker::class], "JumpToTargetThinker")
class JumpToTargetThinker(
    private val jumpCooldown: Float,
    private val highGroundThreshold: Float,
) : Thinker {
    private var sinceJump = jumpCooldown

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        sinceJump += delta

        val myAABB = operatedObject.body.getAABB()
        val targetAABB = screen.player.body.getAABB()

        if (sinceJump >= jumpCooldown && targetAABB.y - myAABB.y > highGroundThreshold && operatedObject.isOnGround) {
            sinceJump = 0f
            operatedObject.getBehaviour<JumpAction>()?.jump(delta, operatedObject, screen)
        }
    }
}
