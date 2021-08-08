package io.github.advancerman.todd.thinker

import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen

interface Thinker {
    fun think(delta: Float, operatedObject: Creature, screen: GameScreen)
}
