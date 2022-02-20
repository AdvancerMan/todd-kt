package io.github.advancerman.todd.objects.creature.behaviour.impl

import com.badlogic.gdx.math.Vector2
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.Behaviour
import io.github.advancerman.todd.objects.creature.behaviour.MoveHorizontallyAction
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.thinker.operated.ThinkerAction

@SerializationType([Behaviour::class], "MoveHorizontallyBehaviour")
open class MoveHorizontallyBehaviour(private val moveSpeed: Float) : MoveHorizontallyAction {
    private var preRunVelocity: Float = 0f

    override fun update(delta: Float, operatedObject: Creature, screen: GameScreen) {
        preRunVelocity = 0f
    }

    override fun prePhysicsUpdate(delta: Float, operatedObject: Creature, screen: GameScreen) {
        val body = operatedObject.body
        body.applyLinearImpulseToCenter(Vector2(preRunVelocity - body.getVelocity().x, 0f))
    }

    override fun moveHorizontally(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen,
        toRight: Boolean,
        speedScale: Float,
    ) {
        moveHorizontally(operatedObject, screen, toRight, moveSpeed * speedScale)
    }

    protected fun moveHorizontally(
        operatedObject: Creature,
        screen: GameScreen,
        toRight: Boolean,
        speed: Float,
    ) {
        operatedObject.reportAnimationEvent(MoveHorizontallyAction.MOVE_EVENT)
        preRunVelocity = if (toRight) speed else -speed

        if (toRight) {
            screen.listenAction(ThinkerAction.MOVE_RIGHT, operatedObject)
        } else {
            screen.listenAction(ThinkerAction.MOVE_LEFT, operatedObject)
        }
    }
}
