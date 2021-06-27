package com.company.todd.json.deserialization

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.ToddGame

class JsonType<T>(val typeName: String, val constructor: (ToddGame?, JsonValue) -> T)

val string = JsonType("string") { _, json -> json.asString() }

val int = JsonType("integer") { _, json -> json.asInt() }

val float = JsonType("float") { _, json -> json.asFloat() }

val vector = JsonType("vector (2-element array)") { _, json ->
    json.asFloatArray().let { Vector2(it[0], it[1]) }
}

val intRectangle = JsonType("integer rectangle (4-element array)") { _, json ->
    json.asIntArray().map { it.toFloat() }.let { Rectangle(it[0], it[1], it[2], it[3]) }
}

val rectangle = JsonType("rectangle (4-element array)") { _, json ->
    json.asFloatArray().let { Rectangle(it[0], it[1], it[2], it[3]) }
}

inline fun <reified T> JsonType<T>.toArray(typeName: String) =
        JsonType(typeName) { game, json -> json.map { constructor(game, it) }.toTypedArray() }

val vectorArray = vector.toArray("array of vectors (2-element arrays)")

val intRectangleArray = intRectangle.toArray("array of integer rectangles (4-element arrays)")