package com.company.todd.thinker.operated

import com.company.todd.objects.creature.Creature
import com.company.todd.screen.game.GameScreen
import com.company.todd.thinker.Thinker

class ScheduledThinker : Thinker {
    private val actionsForTick = mutableMapOf<Long, MutableSet<ThinkerAction>>()

    fun addActionAt(tick: Long, action: ThinkerAction) {
        actionsForTick.getOrPut(tick) { mutableSetOf() }.add(action)
    }

    fun addActionAtLeastAt(tick: Long, action: ThinkerAction) {
        for (i in tick..tick + 2) {
            val actions = actionsForTick.getOrPut(i) { mutableSetOf() }
            if (actions.add(action)) {
                break
            }
        }
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        actionsForTick.remove(screen.tick)?.forEach { it.action(delta, operatedObject, screen) }
    }
}
