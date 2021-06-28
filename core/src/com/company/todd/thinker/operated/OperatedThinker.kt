package com.company.todd.thinker.operated

import com.company.todd.objects.creature.Creature
import com.company.todd.screen.GameScreen
import com.company.todd.thinker.Thinker

class OperatedThinker : Thinker {
    var action: ThinkerAction? = null

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        action?.action?.invoke(delta, operatedObject, screen)
    }
}