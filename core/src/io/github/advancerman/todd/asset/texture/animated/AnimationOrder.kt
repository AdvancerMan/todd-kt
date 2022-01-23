package io.github.advancerman.todd.asset.texture.animated

import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.json.ManualJsonConstructor
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.json.deserialization.checkContains

@SerializationType([AnimationOrder::class])
data class AnimationOrder(
    val nextAnimationType: AnimationType,
    val on: List<String>,
    val resetTime: Boolean = true,
) {
    companion object {
        @ManualJsonConstructor
        fun manualConstructor(json: JsonValue, parsed: MutableMap<String, Any?>) {
            checkContains(json, "on", "list of events that trigger animation change") {
                it.isArray && it.all { inner -> inner.isString }
            }
            parsed["on"] = json["on"].map { it.asString() }
        }
    }
}
