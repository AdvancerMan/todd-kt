package io.github.advancerman.todd.thinker

import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.json.JsonSaveSerializable
import io.github.advancerman.todd.json.ManualJsonConstructor
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.json.deserialization.construct
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.screen.game.GameScreen

/**
 * Sequential thinker runner.
 *
 * ## Behaviour
 *
 * Sequentially runs code for each thinker.
 *
 * @param thinkers Thinkers sequence to ask for actions
 */
@SerializationType([Thinker::class], "SequentialThinker")
class SequentialThinker(@JsonSaveSerializable val thinkers: List<Thinker>) : Thinker {
    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        thinkers.forEach { it.think(delta, operatedObject, screen) }
    }

    companion object {
        @ManualJsonConstructor
        private fun manualConstructor(json: JsonValue, parsed: MutableMap<String, Any?>) {
            json["thinkers"]?.map { it.construct<Thinker>() }
                ?.also { parsed["thinkers"] = it }
        }
    }
}
