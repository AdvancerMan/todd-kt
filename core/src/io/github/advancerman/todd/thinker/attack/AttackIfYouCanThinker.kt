package io.github.advancerman.todd.thinker.attack

import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.AttackAction
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.thinker.Thinker

/**
 * Thinker that triggers attack every frame.
 *
 * ## Behaviour
 *
 * On each frame it checks if attack can be performed.
 * If check succeeds operated object will perform attack.
 *
 */
@SerializationType([Thinker::class], "AttackIfYouCanThinker")
class AttackIfYouCanThinker : Thinker {
    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        operatedObject.getBehaviour<AttackAction>()?.let { attackAction ->
            if (attackAction.canAttack()) {
                attackAction.attack(delta, operatedObject, screen)
            }
        }
    }
}
