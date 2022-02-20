package io.github.advancerman.todd.thinker.operated

import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.objects.creature.behaviour.MoveHorizontallyAction
import io.github.advancerman.todd.objects.creature.behaviour.JumpAction
import io.github.advancerman.todd.objects.creature.behaviour.AttackAction

enum class ThinkerAction(val action: (Float, Creature, GameScreen) -> Unit) {
    MOVE_LEFT({ delta, operatedObject, screen ->
        operatedObject.getBehaviour<MoveHorizontallyAction>()?.moveHorizontally(delta, operatedObject, screen, false)
        operatedObject.isDirectedToRight = false
    }),
    MOVE_RIGHT({ delta, operatedObject, screen ->
        operatedObject.getBehaviour<MoveHorizontallyAction>()?.moveHorizontally(delta, operatedObject, screen, true)
        operatedObject.isDirectedToRight = true
    }),
    JUMP({ delta, operatedObject, screen ->
        operatedObject.getBehaviour<JumpAction>()?.jump(delta, operatedObject, screen)
    }),
    ATTACK({ delta, operatedObject, screen ->
        operatedObject.getBehaviour<AttackAction>()?.attack(delta, operatedObject, screen)
    });
}
