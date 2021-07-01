package com.company.todd.thinker.operated

import com.company.todd.objects.creature.Creature
import com.company.todd.screen.game.ClientGameScreen
import com.company.todd.screen.game.GameScreen
import com.company.todd.screen.game.ServerGameScreen
import com.company.todd.thinker.Thinker
import kotlin.math.max
import kotlin.math.min

class ServerThinker : Thinker {
    private val actionDurations = mutableMapOf<ThinkerAction, Long>()

    fun addAction(action: ClientGameScreen.Action) {
        if (action.action == ThinkerAction.RUN_RIGHT) {
            actionDurations[ThinkerAction.RUN_LEFT] = 0
        } else if (action.action == ThinkerAction.RUN_LEFT) {
            actionDurations[ThinkerAction.RUN_RIGHT] = 0
        }

        val was = actionDurations[action.action] ?: 0
        actionDurations[action.action] = action.duration + min(action.duration, was)
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        actionDurations.filter { it.value > 0 }.forEach { it.key.action(delta, operatedObject, screen) }
        val actualDelta = (screen as ServerGameScreen).fromLastUpdate
        actionDurations.map { it.key to max(0, it.value - actualDelta) }
            .forEach { actionDurations[it.first] = it.second }
    }
}
