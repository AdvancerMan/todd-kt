package com.company.todd.json.deserialization

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

typealias ManualConstructor = (JsonValue, MutableMap<String, Pair<Any?, Boolean>>) -> Unit

private fun getFromJson(
    name: String, clazz: KClass<*>, json: JsonValue, game: ToddGame,
    constructors: Map<String, Map<String, JsonType<*>>>
): Pair<Any?, Boolean> {
    val jsonByName = json[name]
    return when {
        name == "game" -> game to true
        // TODO drawable resource leak on exception
        clazz.isSubclassOf(MyDrawable::class) -> {
            val lowerName = name.lowercase()
            val jsonDrawableName = when {
                lowerName.endsWith("drawablename") -> name
                lowerName.endsWith("drawable") -> name + "Name"
                else -> name + "DrawableName"
            }
            val jsonDrawableObject = jsonDrawableName.substring(0, jsonDrawableName.length - 4)

            val result = json[jsonDrawableName]?.asString()
                ?.let { game.textureManager.loadDrawable(it) }
                ?: json[jsonDrawableObject]
                    ?.let { it ->
                        if (it.isNull) {
                            null
                        } else {
                            game.textureManager.loadDrawable(it["name", string]).apply {
                                myZIndex = it["zIndex", int, game, 0]
                            }
                        }
                    }

            result?.let { it to true } ?: null to false
        }
        clazz.isSubclassOf(BodyPattern::class) -> {
            val bodyPatternJson = jsonByName ?: json
            // expecting name = "bodyPattern"
            parseJsonValue(game, bodyPatternJson, constructors[name]!!, "bodyPatternType") to true
        }
        jsonByName == null -> null to false
        clazz in jsonPrimitives.keys -> json[name, jsonPrimitives[clazz]!!, game] to true
        name in constructors.keys -> parseJsonValue(game, jsonByName, constructors[name]!!) to true
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
    val constructorTypes = data.constructor!!.parameters.map { it.type.jvmErasure }
    val instanceParameter = data.constructor.instanceParameter?.type?.jvmErasure?.objectInstance
    val parameters = data.parametersName!!
        .zip(if (instanceParameter == null) constructorTypes else constructorTypes.drop(1))
    return JsonType(data.constructorDescriptor) { game, json ->
        val parametersFromJson = parameters
            .associate {
                it.first to getFromJson(it.first, it.second, json, game!!, jsonConstructors)
            }
            .toMutableMap()
        manualConstructors[data.constructorDescriptor]!!.invoke(json, parametersFromJson)

        val notProvided = parametersFromJson.filter { !it.value.second }
        if (notProvided.isNotEmpty()) {
            throw IllegalArgumentException("Json is invalid, some parameters were not provided: ${notProvided.keys}")
        }
        val parametersArray = parameters.map { parametersFromJson[it.first]!!.first }.toTypedArray()
        if (instanceParameter == null) {
            data.constructor.call(*parametersArray)
        } else {
            data.constructor.call(instanceParameter, *parametersArray)
        }
    }
}

val jsonConstructors: Map<String, Map<String, JsonType<*>>> by lazy {
    val manualConstructors = mutableMapOf<String, ManualConstructor>()
    Reflection.serializationTypeData
        .onEach { addManualConstructor(it, manualConstructors) }
        .filter { it.constructor != null }
        .groupBy { it.serializationCategory!! }
        .mapValues { (_, constructors) ->
            constructors.associateBy { it.serializationType!! }
                .mapValues { getJsonType(it.value, manualConstructors) }
        }
}

object Constructors {
    val igoConstructors: Map<String, JsonType<out InGameObject>>

    val b2dTypes = mapOf(
        "dynamic" to BodyDef.BodyType.DynamicBody,
        "kinematic" to BodyDef.BodyType.KinematicBody,
        "static" to BodyDef.BodyType.StaticBody
    )

    init {
        @Suppress("UNCHECKED_CAST")
        this.igoConstructors = jsonConstructors["igo"] as Map<String, JsonType<out InGameObject>>
    }
}
