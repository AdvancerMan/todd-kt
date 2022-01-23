package io.github.advancerman.todd.json.deserialization

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.json.deserialization.exception.DeserializationException
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.util.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

typealias ManualConstructor = (JsonValue, MutableMap<String, Any?>) -> Unit

private fun getFromJson(
    name: String,
    clazz: KClass<*>,
    json: JsonValue,
    game: ToddGame?,
    constructors: Map<KClass<*>, Map<String, JsonType<out Any?>>>
): Pair<Any?, Boolean> {
    val jsonByName = json[name]
    return try {
        when {
            clazz == ToddGame::class -> game!! to true
            jsonByName == null -> null to false
            clazz in jsonPrimitives.keys -> json[name, jsonPrimitives[clazz]!!, game] to true
            // TODO drawable resource leak on exception
            clazz in constructors.keys -> parseCompiledJsonValue(game, jsonByName, constructors[clazz]!!) to true
            else -> null to false
        }
    } catch (e: DeserializationException) {
        // TODO this log spams too much on animation frames without xywh, do something with it
        // Gdx.app.error("Constructors", "Could not parse $clazz", e)
        null to false
    }
}

private fun addManualConstructor(
    data: Reflection.SerializationTypeData,
    manualConstructors: MutableMap<String, ManualConstructor>
) {
    val manualConstructor: ManualConstructor? = data.manualConstructor?.let { constructor ->
        { json, map -> constructor.call(data.manualConstructorInstance, json, map) }
    }

    manualConstructors[data.constructorDescriptor] = { json, map ->
        data.superclass?.let { manualConstructors[it.jvmName]!!.invoke(json, map) }
        manualConstructor?.invoke(json, map)
    }
}

private fun getJsonType(
    data: Reflection.SerializationTypeData,
    manualConstructors: MutableMap<String, ManualConstructor>
): JsonType<*> {
    val instanceObject = data.constructor!!.instanceParameter?.type?.jvmErasure?.objectInstance
    val instanceParameter = data.constructor.instanceParameter

    val constructorTypes = data.constructor.parameters.map { it.type.jvmErasure }
        .zip(data.constructor.parameters)
        .let { if (instanceParameter == null) it else it.drop(1) }
    val parameters = data.parametersName!!
        .zip(constructorTypes)
        .map { Triple(it.first, it.second.first, it.second.second) }

    return JsonType(data.serializationType!!) { game, json ->
        val parametersFromJson = parameters
            .map { (name, clazz, _) ->
                name to getFromJson(name, clazz, json, game, jsonConstructors)
            }
            .filter { it.second.second }
            .associate { it.first to it.second.first }
            .toMutableMap()
        manualConstructors[data.constructorDescriptor]!!.invoke(json, parametersFromJson)

        val notProvided = parameters.filter { (name, _, parameter) ->
            !parameter.isOptional && name !in parametersFromJson.keys
        }
        if (notProvided.isNotEmpty()) {
            throw DeserializationException(
                json,
                "Json is invalid, some non-optional parameters " +
                        "were not provided: ${notProvided.map { it.first }}"
            )
        }

        val parametersMap = parameters
            .mapNotNull { (name, _, parameter) ->
                if (name in parametersFromJson) {
                    // parametersFromJson[name] can be null
                    parameter to parametersFromJson[name]
                } else {
                    null
                }
            }
            .associate { it }
        if (instanceParameter == null) {
            data.constructor.callBy(parametersMap)
        } else {
            data.constructor.callBy(parametersMap + mapOf(instanceParameter to instanceObject))
        }
    }
}

val jsonConstructors: Map<KClass<*>, Map<String, JsonType<*>>> by lazy {
    val manualConstructors = mutableMapOf<String, ManualConstructor>()
    Reflection.serializationTypeData
        .onEach { addManualConstructor(it, manualConstructors) }
        .filter { it.constructor != null }
        .flatMap { data ->
            val jsonType = getJsonType(data, manualConstructors)
            data.baseClasses!!.map { it to (data.serializationType!! to jsonType) }
        }
        .groupBy { it.first }
        .mapValues { (_, constructors) -> constructors.associate { it.second } }
}

object Constructors {
    val b2dTypes = mapOf(
        "dynamic" to BodyDef.BodyType.DynamicBody,
        "kinematic" to BodyDef.BodyType.KinematicBody,
        "static" to BodyDef.BodyType.StaticBody
    )
}
