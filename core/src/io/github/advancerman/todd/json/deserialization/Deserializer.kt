package io.github.advancerman.todd.json.deserialization

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.json.JsonUpdateSerializable
import io.github.advancerman.todd.json.ManuallyJsonSerializable
import io.github.advancerman.todd.json.deserialization.exception.DeserializationException
import io.github.advancerman.todd.json.serialization.getJsonName
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.thinker.operated.ThinkerAction
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

fun getJsonErrorMessage(json: JsonValue, message: String) = "$message, json: $json"

inline fun checkContains(
    json: JsonValue,
    key: String,
    shouldBe: String,
    checker: (JsonValue) -> Boolean = { true }
) {
    val value = json[key] ?: throw DeserializationException(
        getJsonErrorMessage(json, "Json should contain $key")
    )

    if (!checker(value)) {
        throw DeserializationException(
            getJsonErrorMessage(json, "$key should be $shouldBe")
        )
    }
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
                ?: throw DeserializationException(
                        getJsonErrorMessage(
                                this,
                                "Json must contain $name" +
                                        (defaultOther?.let { " (or $it as default)" } ?: "") +
                                        ": ${type.typeName}"
                        )
                )

fun <T> parseNonPrototypeJsonValue(game: ToddGame?, json: JsonValue,
                                   constructors: Map<String, JsonType<out T>>): T {
    if (!constructors.containsKey("")) {
        checkContains(json, "type", "object type, one of strings ${constructors.keys}") { checkJson ->
            checkJson.isString && constructors.containsKey(checkJson.asString())
        }
    }

    val constructor = json["type"]?.asString()?.let { jsonType ->
        constructors[jsonType]?.constructor
            ?: throw DeserializationException("Invalid type '$jsonType' for json: $json")
    } ?: constructors[""]!!.constructor
    return try {
        constructor(game, json)
    } catch (e: Exception) {
        throw DeserializationException("Could not parse json, json: $json", e)
    }
}

// TODO use JsonValue.construct instead
fun <T> parseJsonValue(game: ToddGame?, jsonWithPrototype: JsonValue,
                       constructors: Map<String, JsonType<out T>>): T {
    return parseNonPrototypeJsonValue(game, createJsonValue(jsonWithPrototype), constructors)
}

inline fun <reified T> JsonValue.construct(game: ToddGame? = null): T {
    @Suppress("UNCHECKED_CAST")
    val constructors = jsonConstructors[T::class] as? Map<String, JsonType<out T>>
        ?: throw DeserializationException("Invalid base type ${T::class.simpleName} for json constructor")
    return parseJsonValue(game, this, constructors)
}

fun parseInGameObject(jsonWithPrototype: JsonValue): (ToddGame) -> InGameObject = {
    jsonWithPrototype.construct(it)
}

internal val jsonPrimitives = mapOf<KClass<*>, JsonType<*>>(
    String::class to string,
    Vector2::class to vector,
    Rectangle::class to rectangle,
    Boolean::class to boolean,
    Float::class to float,
    Int::class to int,
    Long::class to long,
    ThinkerAction::class to thinkerAction,
    JsonValue::class to identityJson,
    BodyDef.BodyType::class to b2dType
)

private val cachedSchemas = mutableMapOf<KClass<*>, Any.(JsonValue) -> Unit>()

fun Any.updateFromJson(json: JsonValue) {
    cachedSchemas.getOrPut(this::class) { getSchema(this::class) }(this, json)
}

private fun getSchema(clazz: KClass<*>): Any.(JsonValue) -> Unit {
    val cachedData = (listOf(clazz) + clazz.allSuperclasses)
        .flatMap { it.declaredMemberProperties }
        .mapNotNull { property ->
            property.annotations.find { it is JsonUpdateSerializable }
                ?.let { Triple(property, jsonPrimitives[property.returnType.jvmErasure], getJsonName(property, it)) }
        }
        .onEach { it.first.isAccessible = true }

    return { json ->
        cachedData.forEach { (property, jsonReturnType, name) ->
            if (jsonReturnType != null) {
                (property as KMutableProperty1).setter.call(this, json[name, jsonReturnType])
            } else {
                property.call(this)?.updateFromJson(json[name])
            }
        }

        if (this is ManuallyJsonSerializable) {
            deserializeUpdates(json)
        }
    }
}
