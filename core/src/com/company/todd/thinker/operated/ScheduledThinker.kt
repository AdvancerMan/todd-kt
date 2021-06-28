package com.company.todd.thinker.operated

import com.company.todd.objects.creature.Creature
import com.company.todd.screen.GameScreen
import com.company.todd.thinker.Thinker
import java.util.*

class ScheduledThinker(var moment: Float) : Thinker {
    private val actions = TreeSet<Pair<Float, ThinkerAction>>()

    fun addAction(atMoment: Float, action: ThinkerAction) {
        actions.add(atMoment to action)
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        moment += delta
        actions.takeWhile { it.first < moment }
            .map { it.second }
            .toSet()
            .forEach { it.action(delta, operatedObject, screen) }
    }
}
