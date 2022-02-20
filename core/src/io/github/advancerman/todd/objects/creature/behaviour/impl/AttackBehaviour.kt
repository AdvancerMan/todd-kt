package io.github.advancerman.todd.objects.creature.behaviour.impl

import io.github.advancerman.todd.asset.texture.animated.ToddAnimationEvent
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.AttackAction
import io.github.advancerman.todd.objects.creature.behaviour.Behaviour
import io.github.advancerman.todd.objects.weapon.Weapon
import io.github.advancerman.todd.objects.weapon.WithCalculableAttackedObjects
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.thinker.operated.ThinkerAction

@SerializationType([Behaviour::class], "AttackBehaviour")
class AttackBehaviour(private val weapon: Weapon) : AttackAction {
    override fun init(operatedObject: Creature, screen: GameScreen) {
        operatedObject.addActor(weapon)
        weapon.init(operatedObject, screen)
    }

    override fun postUpdate(delta: Float, operatedObject: Creature, screen: GameScreen) {
        weapon.postUpdate(delta)
    }

    override fun attack(delta: Float, operatedObject: Creature, screen: GameScreen) {
        if (canAttack()) {
            weapon.attack()
            operatedObject.reportAnimationEvent(ToddAnimationEvent.ATTACK)
        }
        screen.listenAction(ThinkerAction.ATTACK, operatedObject)
    }

    override fun canAttack(): Boolean = weapon.canAttack()

    override fun getAttackedObjects(): List<InGameObject> =
        (weapon as? WithCalculableAttackedObjects)?.calculateAttackedObjects()?.toList() ?: listOf()
}
