package io.github.advancerman.todd.objects.creature.behaviour.impl

import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.Behaviour
import io.github.advancerman.todd.objects.creature.behaviour.JumpAction
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.thinker.operated.ThinkerAction

@SerializationType([Behaviour::class], "JumpBehaviour")
class JumpBehaviour(private val jumpPower: Float) : JumpAction {
    override fun jump(delta: Float, operatedObject: Creature, screen: GameScreen) {
        if (operatedObject.isOnGround) {
            operatedObject.body.setYVelocity(jumpPower)
            operatedObject.reportAnimationEvent(JumpAction.JUMP_EVENT)
        }
        screen.listenAction(ThinkerAction.JUMP, operatedObject)
    }
}
