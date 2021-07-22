package com.company.todd.json.deserialization

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.util.PROTOTYPES_PATH
import com.company.todd.util.files.crawlJsonListsWithComments

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
        else -> throw IllegalArgumentException(getJsonErrorMessage(
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
                ?: throw IllegalArgumentException(getJsonErrorMessage(it, "Prototype should contain parameter \"protoName\""))
        }
        .onEach { it.value.remove("protoName") }
}

private fun insertPrototype(json: JsonValue, prototype: JsonValue) {
    if (!prototype.isObject) {
        throw IllegalArgumentException(
            "Prototype should be an object. " +
                    "Json: $json. Prototype: $prototype."
        )
    } else if (!json.isObject) {
        throw IllegalArgumentException(
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
            throw IllegalArgumentException("Prototype name should be a string: $it")
        }
        val prototype = prototypes[it.asString()]
            ?: throw IllegalArgumentException("Unknown prototype ${it.asString()}. Json: $json")
        insertPrototype(json, prototype)
    }
}

fun createJsonValue(jsonWithPrototype: JsonValue): JsonValue {
    val result = jsonWithPrototype.cpy()
    prototypesDfs(result)
    return result
}
