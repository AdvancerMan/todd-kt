package com.company.todd.json

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.ToddGame

class JsonType<T>(val typeName: String, val constructor: (ToddGame?, JsonValue) -> T)

val string = JsonType("string") { _, json -> json.asString() }

val float = JsonType("float") { _, json -> json.asFloat() }

val vector = JsonType("vector (2-element array)") { _, json ->
    json.asFloatArray().let { Vector2(it[0], it[1]) }
}

val rectangle = JsonType("rectangle (4-element array)") { _, json ->
    json.asFloatArray().let { Rectangle(it[0], it[1], it[2], it[3]) }
}

val vectorArray = JsonType("array of vectors (2-element arrays)") { game, json ->
    json.map { vector.constructor(game, it) }.toTypedArray()
}
