package io.github.advancerman.todd.objects.creature.behaviour

import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen

interface FlyAction : Behaviour {
    fun flyVertically(delta: Float, operatedObject: Creature, screen: GameScreen, toUp: Boolean)

    fun land()

    fun takeOff()

    val isLanded: Boolean
}
