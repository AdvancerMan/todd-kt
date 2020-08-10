package com.company.todd.json

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.ToddGame
import com.company.todd.launcher.assetsFolder
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.asset.texture.checkContains
import com.company.todd.util.files.crawlJsonListsWithComments

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

operator fun <T> JsonValue.get(name: String, type: JsonType<T>, game: ToddGame? = null) =
        this[name]?.let { type.constructor(game, it) }
                ?: throw IllegalArgumentException("Json must contain $name: ${type.typeName}. Json: $this")

const val prototypesPath = "prototypes"

val prototypes by lazy {
    crawlJsonListsWithComments(assetsFolder + prototypesPath)
            .associateBy {
                it["name"].asString()
                        ?: throw IllegalArgumentException("Prototype should contain parameter \"name\". Json: $it")
            }
}

private fun createJson(
        jsonWithPrototype: JsonValue,
        out: JsonValue = JsonValue(JsonValue.ValueType.`object`)
): JsonValue {
    jsonWithPrototype.forEach {
        if (it.name != null && it.name != "prototype" && !out.has(it.name)) {
            out.addChild(it.name, it)
        }
    }

    return jsonWithPrototype["prototype"]?.asString()?.let { name ->
        val prototype = prototypes[name]
        if (prototype == null) {
            Gdx.app.error("Json", "prototype name $name was not found in prototype map")
            null
        } else {
            createJson(prototype, out)
        }
    } ?: out
}

// FIXME parseInGameObject cannot parse not passive objects
fun parseInGameObject(jsonWithPrototype: JsonValue): (ToddGame) -> InGameObject = {
    val json = createJson(jsonWithPrototype)
    checkContains(json, "type", "object type, one of strings ${passiveConstructors.keys}") { checkJson ->
        checkJson.isString && passiveConstructors.containsKey(checkJson.asString())
    }
    passiveConstructors[json["type"].asString()]!!.constructor(it, json)
}
