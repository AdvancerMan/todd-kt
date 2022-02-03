package io.github.advancerman.todd.objects.creature.behaviour.impl

import com.badlogic.gdx.math.Vector2
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.Behaviour
import io.github.advancerman.todd.objects.creature.behaviour.FlyAction
import io.github.advancerman.todd.screen.game.GameScreen

@SerializationType([Behaviour::class], "BouncyFlyBehaviour")
class BouncyFlyBehaviour(
    private val horizontalFlySpeed: Float,
    private val verticalFlySpeed: Float,
    runSpeed: Float,
) : RunBehaviour(runSpeed), FlyAction {
    private var preVerticalFlyVelocity = 0f

    override fun init(operatedObject: Creature, screen: GameScreen) {
        super<RunBehaviour>.init(operatedObject, screen)
        operatedObject.body.setGravityScale(0f)
    }

    override fun update(delta: Float, operatedObject: Creature, screen: GameScreen) {
        super<RunBehaviour>.update(delta, operatedObject, screen)
        preVerticalFlyVelocity = 0f

        if (operatedObject.isOnGround) {
            operatedObject.body.setGravityScale(1f)
        } else {
            operatedObject.body.setGravityScale(0f)
            // TODO bounce
        }
    }

    override fun prePhysicsUpdate(delta: Float, operatedObject: Creature, screen: GameScreen) {
        super<RunBehaviour>.prePhysicsUpdate(delta, operatedObject, screen)
        val body = operatedObject.body
        body.applyLinearImpulseToCenter(Vector2(0f, preVerticalFlyVelocity - body.getVelocity().y))
    }

    override fun flyHorizontally(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen,
        toRight: Boolean
    ) {
        run(operatedObject, screen, toRight, horizontalFlySpeed)
    }

    override fun flyVertically(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen,
        toUp: Boolean
    ) {
        preVerticalFlyVelocity = if (toUp) verticalFlySpeed else -verticalFlySpeed
    }

    override fun run(delta: Float, operatedObject: Creature, screen: GameScreen, toRight: Boolean) {
        if (operatedObject.isOnGround) {
            super.run(delta, operatedObject, screen, toRight)
        }
    }
}
