package io.github.advancerman.todd.objects.creature.behaviour

import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen

interface AttackAction : Behaviour {
    fun attack(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen
    )

    fun canAttack(): Boolean

    fun getAttackedObjects(): List<InGameObject>
}
