package io.github.advancerman.todd.thinker.fly

import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.AttackAction
import io.github.advancerman.todd.objects.creature.behaviour.FlyAction
import io.github.advancerman.todd.objects.creature.behaviour.MoveHorizontallyAction
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.thinker.Thinker
import io.github.advancerman.todd.util.coordinateDistanceTo

/**
 * AI for witch.
 *
 * ## Behaviour
 *
 * TODO
 *
 */
@SerializationType([Thinker::class], "WitchThinker")
class WitchThinker(
    private val searchingRelaxOnTurnSeconds: Float,
    private val searchingFlySeconds: Float,
    private val chaseTriggerDistance: Float,
    private val attackTriggerDistance: Float,
    private val slowEscapeTriggerDistance: Float,
    private val fastEscapeTriggerDistance: Float,
    private val slowEscapeSpeedScale: Float = 0.5f,
    // TODO primary/secondary attack
    // TODO relaxation logic using land/takeOff
) : Thinker {
    private var sinceSearchStart = 0f

    init {
        require(fastEscapeTriggerDistance < slowEscapeTriggerDistance) {
            "fastEscapeTriggerDistance should be less than slowEscapeTriggerDistance"
        }
        require(slowEscapeTriggerDistance < attackTriggerDistance) {
            "slowEscapeTriggerDistance should be less than attackTriggerDistance"
        }
        require(attackTriggerDistance < chaseTriggerDistance) {
            "attackTriggerDistance should be less than chaseTriggerDistance"
        }
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        val distance = operatedObject.body.getAABB().let { myAabb ->
            screen.player.body.getAABB().let { playerAabb ->
                maxOf(
                    myAabb.x - playerAabb.x - playerAabb.width,
                    playerAabb.x - myAabb.x - myAabb.width,
                    0f,
                )
            }
        }

        val myCenter = operatedObject.body.getCenter()
        val targetCenter = screen.player.body.getCenter()

        when {
            distance <= fastEscapeTriggerDistance -> {
                operatedObject.isDirectedToRight = targetCenter.x < myCenter.x

                moveHorizontallyToTarget(delta, operatedObject, screen.player, screen, true)
            }
            distance <= slowEscapeTriggerDistance -> {
                operatedObject.getBehaviour<AttackAction>()?.attack(delta, operatedObject, screen)
                operatedObject.isDirectedToRight = targetCenter.x > myCenter.x

                moveHorizontallyToTarget(delta, operatedObject, screen.player, screen, true, slowEscapeSpeedScale)
                moveVerticallyToTarget(delta, operatedObject, screen.player, screen, slowEscapeSpeedScale)
            }
            distance <= attackTriggerDistance -> {
                operatedObject.getBehaviour<AttackAction>()?.attack(delta, operatedObject, screen)

                operatedObject.isDirectedToRight = targetCenter.x > myCenter.x
                if (distance > slowEscapeTriggerDistance + PIXEL_EPSILON) {
                    moveHorizontallyToTarget(delta, operatedObject, screen.player, screen)
                }
                moveVerticallyToTarget(delta, operatedObject, screen.player, screen)
            }
            distance <= chaseTriggerDistance -> {
                operatedObject.isDirectedToRight = targetCenter.x > myCenter.x
                moveHorizontallyToTarget(delta, operatedObject, screen.player, screen)
                moveVerticallyToTarget(delta, operatedObject, screen.player, screen)
            }
            else -> {
                val shouldRelax = sinceSearchStart % (searchingFlySeconds + searchingRelaxOnTurnSeconds) > searchingFlySeconds
                if (!shouldRelax) {
                    val moveToRight = (sinceSearchStart / (searchingFlySeconds + searchingRelaxOnTurnSeconds)).toInt() % 2 == 0
                    operatedObject.getBehaviour<MoveHorizontallyAction>()
                        ?.turnAndMoveHorizontally(delta, operatedObject, screen, moveToRight)
                }
            }
        }

        sinceSearchStart += delta
    }

    private fun moveHorizontallyToTarget(
        delta: Float,
        operatedObject: Creature,
        target: InGameObject,
        screen: GameScreen,
        escape: Boolean = false,
        speedScale: Float = 1f,
    ) {
        val myCenter = operatedObject.body.getCenter()
        val targetCenter = target.body.getCenter()

        if (targetCenter.x > myCenter.x + PIXEL_EPSILON) {
            operatedObject.getBehaviour<MoveHorizontallyAction>()
                ?.moveHorizontally(delta, operatedObject, screen, escape.xor(true), speedScale)
        } else if (targetCenter.x < myCenter.x - PIXEL_EPSILON) {
            operatedObject.getBehaviour<MoveHorizontallyAction>()
                ?.moveHorizontally(delta, operatedObject, screen, escape.xor(false), speedScale)
        }
    }

    private fun moveVerticallyToTarget(
        delta: Float,
        operatedObject: Creature,
        target: InGameObject,
        screen: GameScreen,
        speedScale: Float = 1f,
    ) {
        val myCenter = operatedObject.body.getCenter()
        val targetCenter = target.body.getCenter()

        if (targetCenter.y > myCenter.y + PIXEL_EPSILON) {
            operatedObject.getBehaviour<FlyAction>()
                ?.flyVertically(delta, operatedObject, screen, true, speedScale)
        } else if (targetCenter.y < myCenter.y - PIXEL_EPSILON) {
            operatedObject.getBehaviour<FlyAction>()
                ?.flyVertically(delta, operatedObject, screen, false, speedScale)
        }
    }

    companion object {
        private const val PIXEL_EPSILON = 10f
    }
}
