package com.company.todd.json.deserialization

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.json.JsonUpdateSerializable
import com.company.todd.json.ManuallyJsonSerializable
import com.company.todd.json.serialization.getJsonName
import com.company.todd.launcher.ToddGame
import com.company.todd.launcher.assetsFolder
import com.company.todd.objects.base.InGameObject
import com.company.todd.thinker.operated.ThinkerAction
import com.company.todd.util.PROTOTYPES_PATH
import com.company.todd.util.files.crawlJsonListsWithComments
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

fun getJsonErrorMessage(json: JsonValue, message: String) = "$message, json: $json"

inline fun checkContains(json: JsonValue, key: String, shouldBe: String, checker: (JsonValue) -> Boolean) {
    val value = json[key]
            ?: throw IllegalArgumentException(getJsonErrorMessage(json, "Json should contain $key"))

    require(checker(value)) { "$key should be $shouldBe, json: $json" }
}

fun checkName(json: JsonValue, set: Set<String>) {
    checkContains(json, "name", "String (probably this name was already used)") {
        it.isString && !set.contains(it.asString())
    }
}

operator fun <T> JsonValue.get(name: String, type: JsonType<T>, game: ToddGame? = null, default: T? = null, defaultOther: String? = null) =
        this[name]?.let {
            try {
                type.constructor(game, it)
            } catch (e: Exception) {
                Gdx.app.error("Json", getJsonErrorMessage(it, "Unable to create an instance from ${type.typeName} constructor"), e)
                null
            }
        }
                ?: default
                ?: defaultOther?.let { otherName -> this[otherName]?.let { type.constructor(game, it) } }
                ?: throw IllegalArgumentException(
                        getJsonErrorMessage(
                                this,
                                "Json must contain $name" +
                                        (defaultOther?.let { " (or $it as default)" } ?: "") +
                                        ": ${type.typeName}"
                        )
                )


val prototypes by lazy {
    crawlJsonListsWithComments(assetsFolder + PROTOTYPES_PATH)
            .associateBy {
                it["protoName"]?.asString()
                        ?: throw IllegalArgumentException(getJsonErrorMessage(it, "Prototype should contain parameter \"protoName\""))
            }
}

fun createJsonValue(
        jsonWithPrototype: JsonValue,
        out: JsonValue = JsonValue(JsonValue.ValueType.`object`)
): JsonValue {
    jsonWithPrototype.forEach {
        if (it.name != null && it.name !in listOf("prototype", "protoName") && !out.has(it.name)) {
            out.addChild(it.name, it)
        }
    }

    return jsonWithPrototype["prototype"]?.asString()?.let { name ->
        prototypes[name]?.let { createJsonValue(it, out) }
                ?: out.also {
                    Gdx.app.error("Json", getJsonErrorMessage(jsonWithPrototype,
                            "prototype name $name was not found in prototype map")
                    )
                }
    } ?: out
}

fun <T> parseJsonValue(game: ToddGame?, jsonWithPrototype: JsonValue,
                       constructors: Map<String, JsonType<out T>>,
                       jsonTypeName: String = "type"): T {
    val json = createJsonValue(jsonWithPrototype)
    checkContains(json, jsonTypeName, "object type, one of strings ${constructors.keys}") { checkJson ->
        checkJson.isString && constructors.containsKey(checkJson.asString())
    }
    return constructors[json[jsonTypeName].asString()]!!.constructor(game, json)
}

fun parseInGameObject(jsonWithPrototype: JsonValue): (ToddGame) -> InGameObject = {
    parseJsonValue(it, jsonWithPrototype, Constructors.constructors)
}

private val replaceableTypes = mapOf<KClass<*>, JsonType<*>>(
    String::class to string,
    Vector2::class to vector,
    Rectangle::class to rectangle,
    Boolean::class to boolean,
    Float::class to float,
    Int::class to int,
    Long::class to long,
    ThinkerAction::class to thinkerAction
)

fun Any.updateFromJson(json: JsonValue) {
    listOf(listOf(this::class), this::class.allSuperclasses)
        .flatten()
        .flatMap { it.declaredMemberProperties }
        .map { property ->
            property to property.annotations.find { it is JsonUpdateSerializable }
                ?.let { getJsonName(property, it) }
        }
        .filter { it.second != null }
        .forEach { (property, name) ->
            val jsonType = replaceableTypes[property.returnType.jvmErasure]
            property.isAccessible = true
            if (jsonType != null) {
                (property as KMutableProperty1).setter.call(this, json[name!!, jsonType])
            } else {
                property.call(this)?.updateFromJson(json[name!!])
            }
        }

    if (this is ManuallyJsonSerializable) {
        deserializeUpdates(json)
    }
}
