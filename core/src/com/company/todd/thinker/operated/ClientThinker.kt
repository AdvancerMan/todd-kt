package com.company.todd.thinker.operated

import com.badlogic.gdx.Gdx
import com.company.todd.objects.creature.Creature
import com.company.todd.screen.game.ClientGameScreen
import com.company.todd.screen.game.GameScreen
import com.company.todd.thinker.Thinker
import com.company.todd.util.removeWhile
import java.util.TreeSet

// TODO add duration like in ServerThinker
class ClientThinker : Thinker {
    private val actions = mutableMapOf<ThinkerAction, MutableList<Long>>()

    fun addAction(atMoment: Long, action: ThinkerAction) {
        actions.getOrPut(action) { mutableListOf() }.add(atMoment)
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        val now = (screen as ClientGameScreen).updateStartMomentWithPing
        actions.mapNotNull { entry ->
            if (entry.value.removeAll { now - it >= 2000f / Gdx.graphics.framesPerSecond }) {
                entry.key
            } else {
                entry.value.find { it <= now }?.let {
                    entry.value.remove(it)
                    entry.key
                }
            }
        }.forEach { it.action(delta, operatedObject, screen) }
    }
}
