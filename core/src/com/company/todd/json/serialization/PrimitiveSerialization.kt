package com.company.todd.json.serialization

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.json.deserialization.Constructors.b2dTypes

fun String.toJsonValue() = JsonValue(this)

fun Boolean.toJsonValue() = JsonValue(this)

fun Float.toJsonValue() = JsonValue(this.toDouble())

fun Vector2.toJsonValue() = arrayOf(x, y).toJsonValue { JsonValue(it.toDouble()) }

fun BodyDef.BodyType.toJsonValue() = b2dTypes.entries.find { it.value == this }!!.key.toJsonValue()

fun Rectangle.toJsonValue() = arrayOf(x, y, width, height).toJsonValue { JsonValue(it.toDouble()) }

fun <T> Array<T>.toJsonValue(elementToJson: (T) -> JsonValue): JsonValue {
    val jsonArray = JsonValue(JsonValue.ValueType.array)
    forEach { jsonArray.addChild(elementToJson(it)) }
    return jsonArray
}
