package com.company.todd.thinker.operated

import com.company.todd.objects.creature.Creature
import com.company.todd.screen.GameScreen

enum class ThinkerAction(val action: (Float, Creature, GameScreen) -> Unit) {
    RUN_LEFT({ _, obj, _ ->
        obj.run(false)
        obj.isDirectedToRight = false
    }),
    RUN_RIGHT({ _, obj, _ ->
        obj.run(true)
        obj.isDirectedToRight = true
    }),
    JUMP({ _, obj, _ -> obj.jump() }),
    ATTACK({ _, obj, _ -> obj.attack() });
}
