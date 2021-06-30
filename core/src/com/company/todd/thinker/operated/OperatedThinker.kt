package com.company.todd.thinker.operated

import com.company.todd.objects.creature.Creature
import com.company.todd.screen.game.GameScreen
import com.company.todd.thinker.Thinker

class OperatedThinker : Thinker {
    private val actions = mutableSetOf<ThinkerAction>()

    fun addAction(action: ThinkerAction) {
        actions.add(action)
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        actions.forEach { it.action(delta, operatedObject, screen) }
        actions.clear()
    }
}
