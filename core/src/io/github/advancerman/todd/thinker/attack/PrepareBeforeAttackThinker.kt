package io.github.advancerman.todd.thinker.attack

import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.creature.behaviour.AttackAction
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.thinker.Thinker
import io.github.advancerman.todd.util.coordinateDistanceTo
import kotlin.math.floor
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
    private val relaxSeconds: Float,
    private val preparationSeconds: Float,
    private val maxHitTries: Int,
    private val delayBetweenHitsSeconds: Float,
    private val relaxWithoutHitProbability: Float,
) : Thinker {
    private var sincePreparationStart = 0f
    private var triedHits = 0
    private var rolledRelaxation = false
    private val tillPreparationEnd: Float
        get() = preparationSeconds + (maxHitTries - 1) * delayBetweenHitsSeconds

    init {
        if (relaxWithoutHitProbability !in 0f..1f) {
            throw IllegalArgumentException(
                "'relaxProbability' should be in range [0, 1], but got $relaxWithoutHitProbability"
            )
        }
        val mustBePositive = listOf(
            triggerDistancePixels to "triggerDistancePixels",
            relaxSeconds to "relaxSeconds",
            preparationSeconds to "preparationSeconds",
            maxHitTries to "maxHitTries",
            delayBetweenHitsSeconds to "delayBetweenHitsSeconds",
        )
        mustBePositive.forEach { (field, name) ->
            if (field.toFloat() < 0f) {
                throw IllegalArgumentException("'$name' should be positive, but got $field")
            }
        }
        resetPreparation()
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        sincePreparationStart += delta
        if (sincePreparationStart < preparationSeconds) {
            operatedObject.reportAnimationEvent(PREPARATION_EVENT)
        } else if (triedHits < maxHitTries) {
            if (!rolledRelaxation && Random.nextFloat() < relaxWithoutHitProbability) {
                resetPreparation()
            } else if (shouldTryHit(delta)) {
                tryHit(delta, operatedObject, screen)
            } else {
                operatedObject.reportAnimationEvent(PREPARATION_EVENT)
            }
            rolledRelaxation = true
        } else if (sincePreparationStart > tillPreparationEnd + relaxSeconds) {
            val distance = operatedObject.body.getAABB()
                .coordinateDistanceTo(screen.player.body.getAABB())

            if (distance < triggerDistancePixels) {
                initPreparation()
                operatedObject.reportAnimationEvent(PREPARATION_EVENT)
            }
        }
    }

    private fun tryHit(delta: Float, operatedObject: Creature, screen: GameScreen) {
        triedHits++
        if (shouldHit(operatedObject, screen)) {
            operatedObject.getBehaviour<AttackAction>()?.attack(delta, operatedObject, screen)
            resetPreparation()
        } else {
            operatedObject.reportAnimationEvent(PREPARATION_EVENT)
        }
    }

    private fun initPreparation() {
        rolledRelaxation = false
        sincePreparationStart = 0f
        triedHits = 0
    }

    private fun resetPreparation() {
        rolledRelaxation = true
        sincePreparationStart = tillPreparationEnd
        triedHits = maxHitTries
    }

    private fun shouldTryHit(delta: Float) = (sincePreparationStart - preparationSeconds).let {
        floor(it / delayBetweenHitsSeconds) != floor((it + delta) / delayBetweenHitsSeconds)
    }

    private fun shouldHit(operatedObject: Creature, screen: GameScreen): Boolean {
        val result = operatedObject.getBehaviour<AttackAction>()?.let { attackAction ->
            sincePreparationStart >= preparationSeconds
                    && attackAction.canAttack()
                    && attackAction.getAttackedObjects().contains(screen.player)
        }
        return result == true
    }

    companion object {
        private const val PREPARATION_EVENT = "preparation"
    }
}
