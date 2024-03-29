package io.github.advancerman.todd.json.deserialization

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.util.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

typealias ManualConstructor = (JsonValue, MutableMap<String, Pair<Any?, Boolean>>) -> Unit

private fun getFromJson(
    name: String, clazz: KClass<*>, json: JsonValue, game: ToddGame,
    constructors: Map<KClass<*>, Map<String, JsonType<out Any?>>>
): Pair<Any?, Boolean> {
    val jsonByName = json[name]
    return when {
        clazz == ToddGame::class -> game to true
        jsonByName == null -> null to false
        clazz in jsonPrimitives.keys -> json[name, jsonPrimitives[clazz]!!, game] to true
        // TODO drawable resource leak on exception
        clazz in constructors.keys -> parseNonPrototypeJsonValue(game, jsonByName, constructors[clazz]!!) to true
        else -> null to false
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

    return JsonType(data.constructorDescriptor) { game, json ->
        val parametersFromJson = parameters
            .associate {
                it.first to getFromJson(it.first, it.second, json, game!!, jsonConstructors)
            }
            .toMutableMap()
        manualConstructors[data.constructorDescriptor]!!.invoke(json, parametersFromJson)

        val maybeParametersMap = parameters.associate {
            it.third to (it.first to parametersFromJson[it.first]!!)
        }

        val notProvided = maybeParametersMap.filter { !it.key.isOptional && !it.value.second.second }
        if (notProvided.isNotEmpty()) {
            throw IllegalArgumentException(
                "Json is invalid, some non-optional parameters " +
                        "were not provided: ${notProvided.values.map { it.first }}"
            )
        }

        val parametersMap = maybeParametersMap
            .filter { it.value.second.second }
            .mapValues { it.value.second.first }
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
        .groupBy { it.baseClass!! }
        .mapValues { (_, constructors) ->
            constructors.associateBy { it.serializationType!! }
                .mapValues { getJsonType(it.value, manualConstructors) }
        }
}

object Constructors {
    val b2dTypes = mapOf(
        "dynamic" to BodyDef.BodyType.DynamicBody,
        "kinematic" to BodyDef.BodyType.KinematicBody,
        "static" to BodyDef.BodyType.StaticBody
    )
}
