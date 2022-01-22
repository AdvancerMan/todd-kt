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
        else -> throw DeserializationException(getJsonErrorMessage(
            this, "Unknown json type ${type()}"
        ))
    }
    result.name = name
    return result
}

val prototypes by lazy {
    crawlJsonListsWithComments(PROTOTYPES_PATH)
        .associateBy {
            it["protoName"]?.asString()
                ?: throw DeserializationException(getJsonErrorMessage(it, "Prototype should contain parameter \"protoName\""))
        }
        .onEach { it.value.remove("protoName") }
}

private fun insertPrototype(json: JsonValue, prototype: JsonValue) {
    if (!prototype.isObject) {
        throw DeserializationException(
            "Prototype should be an object. " +
                    "Json: $json. Prototype: $prototype."
        )
    } else if (!json.isObject) {
        throw DeserializationException(
            "Could not insert prototype to not object-like json. " +
                    "Json: $json. Prototype: $prototype."
        )
    }

    prototype.forEach {
        if (!json.has(it.name)) {
            json.addChild(it.cpy())
        } else if (it.isObject) {
            insertPrototype(json[it.name], it)
        }
    }
}

private fun prototypesDfs(json: JsonValue) {
    if (!json.isObject) {
        return
    }
    json.forEach { prototypesDfs(it) }
    json.remove("prototype")?.let {
        if (!it.isString) {
            throw DeserializationException("Prototype name should be a string: $it")
        }
        val prototype = prototypes[it.asString()]
            ?: throw DeserializationException("Unknown prototype ${it.asString()}. Json: $json")
        insertPrototype(json, prototype)
    }
}

private fun insertShortcut(
    json: JsonValue,
    shortcut: JsonValue,
    names: MutableList<String>,
    initialName: String
) {
    if (json.type() != JsonValue.ValueType.`object`) {
        throw DeserializationException(getJsonErrorMessage(
            json,
            "Shortcut \n$shortcut\n can not be inserted " +
                    "in a non-object json value (initial name: $initialName)"
        ))
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

fun createJsonValue(jsonWithPrototype: JsonValue): JsonValue {
    val result = jsonWithPrototype.cpy()
    objectShortcutsDfs(result)
    prototypesDfs(result)
    return result
}
