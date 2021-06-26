package com.company.todd.thinker

import com.company.todd.objects.creature.Creature
import com.company.todd.screen.GameScreen

interface Thinker {
    fun think(delta: Float, operatedObject: Creature, screen: GameScreen)
}
