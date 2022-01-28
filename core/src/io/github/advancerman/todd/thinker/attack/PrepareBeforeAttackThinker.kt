package io.github.advancerman.todd.thinker.attack

import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.thinker.Thinker
import io.github.advancerman.todd.util.coordinateDistanceTo
import kotlin.random.Random

/**
 * Thinker for creatures with preparations.
 *
 * ## Behaviour
 *
 * TODO
 *
 */
@SerializationType([Thinker::class], "PrepareBeforeAttackThinker")
class PrepareBeforeAttackThinker(
    private val triggerDistancePixels: Float,
    private val delaySeconds: Float,
    private val minTimeSeconds: Float,
    maxPreparedSeconds: Float,
    private val relaxProbability: Float,
) : Thinker {
    private val maxTimeSeconds = minTimeSeconds + maxPreparedSeconds
    private var sincePreparationStart = maxTimeSeconds + 1f
    private var rolledRelaxation = false

    init {
        if (relaxProbability !in 0f..1f) {
            throw IllegalArgumentException(
                "'relaxProbability' should be in range [0, 1], but got $relaxProbability"
            )
        }
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        if (sincePreparationStart < minTimeSeconds) {
            operatedObject.reportAnimationEvent(PREPARATION_EVENT)
        } else if (sincePreparationStart < maxTimeSeconds) {
            if (!rolledRelaxation && Random.nextFloat() < relaxProbability) {
                sincePreparationStart = maxTimeSeconds - delta
            } else if (shouldAttack(operatedObject, screen)) {
                operatedObject.attack()
                sincePreparationStart = maxTimeSeconds - delta
            } else {
                operatedObject.reportAnimationEvent(PREPARATION_EVENT)
            }
            rolledRelaxation = true
        } else if (sincePreparationStart > maxTimeSeconds + delaySeconds) {
            val distance = operatedObject.body.getAABB()
                .coordinateDistanceTo(screen.player.body.getAABB())

            if (distance < triggerDistancePixels) {
                rolledRelaxation = false
                sincePreparationStart = -delta
                operatedObject.reportAnimationEvent(PREPARATION_EVENT)
            }
        }

        sincePreparationStart += delta
    }

    private fun shouldAttack(operatedObject: Creature, screen: GameScreen): Boolean =
        sincePreparationStart >= minTimeSeconds
                && operatedObject.canAttack()
                && operatedObject.getAttackedObjects().contains(screen.player)

    companion object {
        private const val PREPARATION_EVENT = "preparation"
    }
}
