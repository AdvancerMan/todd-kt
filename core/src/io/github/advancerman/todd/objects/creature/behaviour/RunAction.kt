package io.github.advancerman.todd.objects.creature.behaviour

import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen

interface RunAction : Behaviour {
    fun run(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen,
        toRight: Boolean = operatedObject.isDirectedToRight
    )

    companion object {
        const val RUN_EVENT = "run"
    }
}
