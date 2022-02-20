package io.github.advancerman.todd.objects.creature.behaviour

import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen

interface MoveHorizontallyAction : Behaviour {
    fun moveHorizontally(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen,
        toRight: Boolean = operatedObject.isDirectedToRight,
        speedScale: Float = 1f,
    )

    fun turnAndMoveHorizontally(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen,
        toRight: Boolean = operatedObject.isDirectedToRight,
        speedScale: Float = 1f,
    ) {
        operatedObject.isDirectedToRight = toRight
        moveHorizontally(delta, operatedObject, screen, toRight, speedScale)
    }

    companion object {
        const val MOVE_EVENT = "move"
    }
}
