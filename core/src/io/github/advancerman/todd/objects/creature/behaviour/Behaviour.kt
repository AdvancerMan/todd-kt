package io.github.advancerman.todd.objects.creature.behaviour

import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen

interface Behaviour {
    fun init(operatedObject: Creature, screen: GameScreen) {}
    fun update(delta: Float, operatedObject: Creature, screen: GameScreen) {}
    fun prePhysicsUpdate(delta: Float, operatedObject: Creature, screen: GameScreen) {}
    fun postUpdate(delta: Float, operatedObject: Creature, screen: GameScreen) {}
}
