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
    private val bounceUpSeconds: Float
        get() = bounceDownSpeed / bounceUpSpeed * bounceDownSeconds

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
            preVerticalFlyVelocity = calculateBounceVelocity(sinceBounceStart)
        }
    }

    private fun calculateBounceVelocity(atMoment: Float): Float {
        val period = bounceDownSeconds + bounceUpSeconds
        val phase = (atMoment + bounceDownSeconds / 2f) % period

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
        toUp: Boolean,
        speedScale: Float,
    ) {
        if (!isLanded) {
            if (toUp) {
                preVerticalFlyVelocity = flyUpSpeed * speedScale
                sinceBounceStart = (bounceUpSeconds + bounceDownSeconds) / 2
            } else {
                preVerticalFlyVelocity = -flyDownSpeed * speedScale
                sinceBounceStart = 0f
            }
        }
    }

    override fun land() {
        isLanded = true
    }

    override fun takeOff() {
        isLanded = false
    }

    override fun moveHorizontally(
        delta: Float,
        operatedObject: Creature,
        screen: GameScreen,
        toRight: Boolean,
        speedScale: Float,
    ) {
        if (isLanded) {
            super.moveHorizontally(delta, operatedObject, screen, toRight, speedScale)
        } else {
            super.moveHorizontally(operatedObject, screen, toRight, horizontalFlySpeed * speedScale)
        }
    }
}
