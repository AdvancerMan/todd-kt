package io.github.advancerman.todd.thinker

import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen
import kotlin.random.Random

/**
 * Thinker for creatures with preparations
 *
 * ## Behaviour
 *
 * TODO
 *
 */
@SerializationType([Thinker::class], "PreparingThinker")
class PrepareBeforeAttackThinker(
    triggerDistancePixels: Float,
    private val delaySeconds: Float,
    private val minTimeSeconds: Float,
    maxPreparedSeconds: Float,
    private val relaxProbability: Float,
) : Thinker {
    private val triggerDistancePixels2 = triggerDistancePixels * triggerDistancePixels
    private val maxTimeSeconds = minTimeSeconds + maxPreparedSeconds
    private var sincePreparationStart = 0f
    private var rolledRelaxation = false

    init {
        if (relaxProbability !in 0f..1f) {
            throw IllegalArgumentException(
                "'relaxProbability' should be in range [0, 1], but got $relaxProbability"
            )
        }
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        if (sincePreparationStart < maxTimeSeconds) {
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
            val distance2 = operatedObject.body.getCenter()
                .sub(screen.player.body.getCenter())
                .len2()

            if (distance2 < triggerDistancePixels2) {
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
