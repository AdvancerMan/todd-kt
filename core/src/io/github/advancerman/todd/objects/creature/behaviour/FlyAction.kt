package io.github.advancerman.todd.objects.creature.behaviour

import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen

interface FlyAction : Behaviour {
    fun flyHorizontally(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen,
        toRight: Boolean = operatedObject.isDirectedToRight
    )

    fun flyVertically(delta: Float, operatedObject: Creature, screen: GameScreen, toUp: Boolean)

    fun land()

    fun takeOff()

    val isLanded: Boolean
}
