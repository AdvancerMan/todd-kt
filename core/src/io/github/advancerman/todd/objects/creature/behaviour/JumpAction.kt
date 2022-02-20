package io.github.advancerman.todd.objects.creature.behaviour

import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen

interface JumpAction : Behaviour {
    fun jump(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen
    )
}
