package com.company.todd.thinker.operated

import com.company.todd.objects.creature.Creature
import com.company.todd.screen.game.GameScreen
import com.company.todd.thinker.Thinker
import java.util.TreeSet

class ScheduledThinker(var moment: Float) : Thinker {
    private val actions = TreeSet<Pair<Float, ThinkerAction>> { p1, p2 ->
        when {
            p1.first > p2.first -> 1
            p1.first < p2.first -> -1
            else -> p1.second.compareTo(p2.second)
        }
    }

    fun addAction(atMoment: Float, action: ThinkerAction) {
        actions.add(atMoment to action)
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        moment += delta
        val actionsNow = mutableSetOf<ThinkerAction>()
        actions.removeAll { p ->
            (p.first < moment).also {
                if (it) {
                    actionsNow.add(p.second)
                }
            }
        }
        actionsNow.forEach { it.action(delta, operatedObject, screen) }
    }
}
