package com.company.todd.thinker

import com.company.todd.objects.active.ActiveObject
import com.company.todd.screen.GameScreen

interface Thinker {
    fun think(delta: Float, operatedObject: ActiveObject, screen: GameScreen)
}
