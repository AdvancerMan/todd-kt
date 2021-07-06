package com.company.todd.thinker.operated

import com.company.todd.objects.creature.Creature
import com.company.todd.screen.game.GameScreen
import com.company.todd.thinker.Thinker

class ScheduledThinker : Thinker {
    private val actionsForTick = mutableMapOf<Long, MutableList<ThinkerAction>>()

    fun addAction(tick: Long, action: ThinkerAction) {
        actionsForTick.getOrPut(tick) { mutableListOf() }.add(action)
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        actionsForTick.remove(screen.tick)?.forEach { it.action(delta, operatedObject, screen) }
    }
}
