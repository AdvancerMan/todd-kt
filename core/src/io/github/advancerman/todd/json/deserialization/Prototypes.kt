package io.github.advancerman.todd.json.deserialization

import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.json.deserialization.exception.DeserializationException
import io.github.advancerman.todd.util.PROTOTYPES_PATH
import io.github.advancerman.todd.util.files.crawlJsonListsWithComments

fun JsonValue.cpy(): JsonValue {
    val result = when {
        isNull -> JsonValue(JsonValue.ValueType.nullValue)
        isBoolean -> JsonValue(asBoolean())
        isLong -> JsonValue(asLong())
        isDouble -> JsonValue(asDouble())
        isString -> JsonValue(asString())
        isArray || isObject -> {
            val result = JsonValue(type())
            forEach { result.addChild(it.cpy()) }
            result
        }
        else -> throw DeserializationException(this, "Unknown json type ${type()}")
    }
    result.name = name
    return result
}

private fun createPrototypeValue(
    name: String,
    linkedJson: JsonValue,
    rawPrototypes: Map<String, JsonValue>,
    visited: MutableSet<String>,
    result: MutableMap<String, JsonValue>
): JsonValue = result.getOrPut(name) {
    val json = rawPrototypes[name]?.cpy()
        ?: throw DeserializationException(linkedJson, "Unknown prototype $name")
    if (!visited.add(name)) {
        throw DeserializationException(
            linkedJson,
            "Cyclic dependency detected for prototype '$name'"
        )
    }
    objectShortcutsDfs(json)
    prototypesDfs(json) { name, currentLinkedJson ->
        createPrototypeValue(name, currentLinkedJson, rawPrototypes, visited, result)
    }
    json
}

val prototypes by lazy {
    val rawPrototypes = crawlJsonListsWithComments(PROTOTYPES_PATH)
        .associateBy {
            it["protoName"]?.asString()
                ?: throw DeserializationException(
                    it,
                    "Prototype should contain parameter \"protoName\""
                )
        }
        .onEach { it.value.remove("protoName") }
        .onEach { (_, json) ->
            if (json.has("isDeepPrototype")) {
                if (!json["isDeepPrototype"].isBoolean) {
                    throw DeserializationException(json, "'isDeepPrototype' should be boolean")
                }
            } else {
                json.addChild("isDeepPrototype", JsonValue(false))
            }
        }
    val result = mutableMapOf<String, JsonValue>()
    val visited = mutableSetOf<String>()
    rawPrototypes.forEach { (name, linkedJson) ->
        createPrototypeValue(name, linkedJson, rawPrototypes, visited, result)
    }
    result
}

private fun insertPrototype(json: JsonValue, prototype: JsonValue, isDeep: Boolean) {
    if (!prototype.isObject) {
        throw DeserializationException(json, "Prototype should be an object, prototype: $prototype")
    } else if (!json.isObject) {
        throw DeserializationException(
            json,
            "Could not insert prototype to non-object json, prototype: $prototype"
        )
    }

    prototype.forEach {
        if (it.name == "isDeepPrototype") {
            return@forEach
        } else if (!json.has(it.name)) {
            json.addChild(it.cpy())
        } else if (it.isObject && isDeep) {
            insertPrototype(json[it.name], it, isDeep)
        }
    }
}

private fun prototypesDfs(json: JsonValue, getPrototype: (String, JsonValue) -> JsonValue) {
    if (!json.isObject) {
        return
    }
    json.forEach { prototypesDfs(it, getPrototype) }
    json.remove("prototype")?.let {
        if (!it.isString) {
            json.addChild("prototype", it)
            throw DeserializationException(json, "Prototype name should be a string")
        }
        val prototype = getPrototype(it.asString(), json)
        insertPrototype(json, prototype, prototype["isDeepPrototype"].asBoolean())
    }
}

private fun insertShortcut(
    json: JsonValue,
    shortcut: JsonValue,
    names: MutableList<String>,
    initialName: String
) {
    if (json.type() != JsonValue.ValueType.`object`) {
        throw DeserializationException(
            json,
            "Shortcut \n$shortcut\n can not be inserted " +
                    "in a non-object json value (initial name: $initialName)"
        )
    }
    if (names.size == 1) {
        json.removeAll { it.name == names[0] }
        json.addChild(names[0], shortcut)
    } else {
        val name = names.removeLast()
        if (!json.has(name)) {
            json.addChild(name, JsonValue(JsonValue.ValueType.`object`))
        }
        insertShortcut(json[name], shortcut, names, initialName)
    }
}

private fun objectShortcutsDfs(json: JsonValue) {
    if (!json.isObject) {
        return
    }
    json.forEach { objectShortcutsDfs(it) }
    json.filter { it.name().contains('.') }
        .also { _ -> json.removeAll { it.name().contains('.') } }
        .forEach { shortcut ->
            val names = shortcut.name()
                .split('.')
                .asReversed()
                .toMutableList()
            // shortcut.cpy() is needed to reset all json fields like 'next', 'prev'
            insertShortcut(json, shortcut.cpy(), names, shortcut.name())
        }
}

fun compileJsonValue(json: JsonValue): JsonValue {
    val result = json.cpy()
    objectShortcutsDfs(result)
    prototypesDfs(result) { name, linkedJson ->
        prototypes[name] ?: throw DeserializationException(linkedJson, "Unknown prototype $name")
    }
    return result
}
