package com.company.todd.json.serialization

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue

fun String.toJsonValue() = JsonValue(this)

fun Vector2.toJsonValue() = arrayOf(x, y).toJsonValue { JsonValue(it.toDouble()) }

fun <T> Array<T>.toJsonValue(elementToJson: (T) -> JsonValue): JsonValue {
    val jsonArray = JsonValue(JsonValue.ValueType.array)
    forEach { jsonArray.addChild(elementToJson(it)) }
    return jsonArray
}
