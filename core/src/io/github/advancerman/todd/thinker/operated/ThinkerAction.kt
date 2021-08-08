package io.github.advancerman.todd.thinker.operated

import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen

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
