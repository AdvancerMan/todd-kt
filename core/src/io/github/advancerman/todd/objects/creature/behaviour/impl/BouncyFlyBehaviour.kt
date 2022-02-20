package io.github.advancerman.todd.objects.creature.behaviour.impl

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.Behaviour
import io.github.advancerman.todd.objects.creature.behaviour.FlyAction
import io.github.advancerman.todd.screen.game.GameScreen

@SerializationType([Behaviour::class], "BouncyFlyBehaviour")
class BouncyFlyBehaviour(
    private val horizontalFlySpeed: Float,
    private val flyUpSpeed: Float,
    private val flyDownSpeed: Float,
    private val bounceUpSpeed: Float = flyUpSpeed,
    private val bounceDownSpeed: Float = flyDownSpeed,
    private val bounceDownSeconds: Float,
    runSpeed: Float,
) : MoveHorizontallyBehaviour(runSpeed), FlyAction {
    private var preVerticalFlyVelocity = 0f
    private var sinceBounceStart = 0f
    override var isLanded = false

    override fun init(operatedObject: Creature, screen: GameScreen) {
        super<MoveHorizontallyBehaviour>.init(operatedObject, screen)
        operatedObject.body.setGravityScale(0f)
    }

    override fun update(delta: Float, operatedObject: Creature, screen: GameScreen) {
        super<MoveHorizontallyBehaviour>.update(delta, operatedObject, screen)

        if (isLanded) {
            operatedObject.body.setGravityScale(1f)
            sinceBounceStart = 0f
        } else {
            operatedObject.body.setGravityScale(0f)
            sinceBounceStart += delta
            preVerticalFlyVelocity = calculateBounceVelocity()
        }
    }

    private fun calculateBounceVelocity(): Float {
        val bounceUpSeconds = bounceDownSpeed / bounceUpSpeed * bounceDownSeconds
        val period = bounceDownSeconds + bounceUpSeconds
        val phase = (sinceBounceStart + bounceDownSeconds / 2f) % period

        return if (phase < bounceDownSeconds) {
            val bounceDownPhase = phase / bounceDownSeconds * MathUtils.PI
            -MathUtils.sin(bounceDownPhase) * bounceDownSpeed
        } else {
            val bounceUpPhase = (phase - bounceDownSeconds) / bounceUpSeconds * MathUtils.PI
            MathUtils.sin(bounceUpPhase) * bounceUpSpeed
        }
    }

    override fun prePhysicsUpdate(delta: Float, operatedObject: Creature, screen: GameScreen) {
        super<MoveHorizontallyBehaviour>.prePhysicsUpdate(delta, operatedObject, screen)
        if (!isLanded) {
            val body = operatedObject.body
            body.applyLinearImpulseToCenter(
                Vector2(0f, preVerticalFlyVelocity - body.getVelocity().y)
            )
        }
    }

    override fun flyVertically(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen,
        toUp: Boolean
    ) {
        if (!isLanded) {
            sinceBounceStart = 0f
            preVerticalFlyVelocity = if (toUp) flyUpSpeed else -flyDownSpeed
        }
    }

    override fun land() {
        isLanded = true
    }

    override fun takeOff() {
        isLanded = false
    }

    override fun moveHorizontally(delta: Float, operatedObject: Creature, screen: GameScreen, toRight: Boolean) {
        if (isLanded) {
            super.moveHorizontally(delta, operatedObject, screen, toRight)
        } else {
            super.moveHorizontally(operatedObject, screen, toRight, horizontalFlySpeed)
        }
    }
}
