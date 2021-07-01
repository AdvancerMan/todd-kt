package com.company.todd.thinker.operated

import com.badlogic.gdx.Gdx
import com.company.todd.objects.creature.Creature
import com.company.todd.screen.game.ClientGameScreen
import com.company.todd.screen.game.GameScreen
import com.company.todd.thinker.Thinker
import com.company.todd.util.removeWhile
import java.util.TreeSet

class ClientThinker : Thinker {
    private val actions = TreeSet(compareBy<Pair<Long, ThinkerAction>> { it.first }.thenBy { it.second })

    fun addAction(atMoment: Long, action: ThinkerAction) {
        actions.add(atMoment to action)
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        val now = (screen as ClientGameScreen).updateStartMomentWithPing
        val actionsNow = mutableSetOf<ThinkerAction>()
        actions.removeWhile {
            (it.first <= now && it.second !in actionsNow || now - it.first >= 2000f / Gdx.graphics.framesPerSecond)
                .also { _ -> actionsNow.add(it.second) }
        }
        actionsNow.forEach { it.action(delta, operatedObject, screen) }
    }
}
