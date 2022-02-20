package io.github.advancerman.todd.thinker.run

import io.github.advancerman.todd.json.JsonSaveSerializable
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.MoveHorizontallyAction
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.thinker.Thinker

/**
 * Thinker that runs to target if target is too far from operated object.
 *
 * ## Behaviour
 *
 * Runs in target's direction until x coordinate difference is more than [maxDistanceFromTarget].
 *
 */
@SerializationType([Thinker::class], "RunToTargetThinker")
class RunToTargetThinker(
    @JsonSaveSerializable private val maxDistanceFromTarget: Float
) : Thinker {
    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        val myAABB = operatedObject.body.getAABB()
        val targetAABB = screen.player.body.getAABB()

        if (targetAABB.x - myAABB.x - myAABB.width > maxDistanceFromTarget) {
            operatedObject.isDirectedToRight = true
            operatedObject.getBehaviour<MoveHorizontallyAction>()?.moveHorizontally(delta, operatedObject, screen, true)
        } else if (targetAABB.x + targetAABB.width - myAABB.x < -maxDistanceFromTarget) {
            operatedObject.isDirectedToRight = false
            operatedObject.getBehaviour<MoveHorizontallyAction>()?.moveHorizontally(delta, operatedObject, screen, false)
        }
    }
}
