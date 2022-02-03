package io.github.advancerman.todd.objects.creature.behaviour.impl

import com.badlogic.gdx.math.Vector2
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.Behaviour
import io.github.advancerman.todd.objects.creature.behaviour.RunAction
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.thinker.operated.ThinkerAction

@SerializationType([Behaviour::class], "RunBehaviour")
open class RunBehaviour(private val runSpeed: Float) : RunAction {
    private var preRunVelocity: Float = 0f

    override fun update(delta: Float, operatedObject: Creature, screen: GameScreen) {
        preRunVelocity = 0f
    }

    override fun prePhysicsUpdate(delta: Float, operatedObject: Creature, screen: GameScreen) {
        val body = operatedObject.body
        body.applyLinearImpulseToCenter(Vector2(preRunVelocity - body.getVelocity().x, 0f))
    }

    override fun run(delta: Float, operatedObject: Creature, screen: GameScreen, toRight: Boolean) {
        run(operatedObject, screen, toRight, runSpeed)
    }

    protected fun run(
        operatedObject: Creature,
        screen: GameScreen,
        toRight: Boolean,
        speed: Float,
    ) {
        operatedObject.reportAnimationEvent(RunAction.RUN_EVENT)
        preRunVelocity = if (toRight) speed else -speed

        if (toRight) {
            screen.listenAction(ThinkerAction.RUN_RIGHT, operatedObject)
        } else {
            screen.listenAction(ThinkerAction.RUN_LEFT, operatedObject)
        }
    }
}
