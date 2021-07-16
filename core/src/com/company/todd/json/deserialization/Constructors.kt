package com.company.todd.json.deserialization

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.box2d.bodyPattern.createCircleBP
import com.company.todd.box2d.bodyPattern.createPolygonBPWithTGS
import com.company.todd.box2d.bodyPattern.createRectangleBPWithTGS
import com.company.todd.box2d.bodyPattern.createRectangleBPWithTGSBGS
import com.company.todd.json.*
import com.company.todd.json.serialization.getJsonName
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.Reflection
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

private fun findSerializationNameAnnotation(property: KProperty1<*, *>) =
    property.findAnnotation<JsonUpdateSerializable>()
        ?: property.findAnnotation<JsonFullSerializable>()
        ?: property.findAnnotation<JsonSaveSerializable>()

private fun getPropertyJsonNames(clazz: KClass<*>): Map<String, String> {
    return (listOf(clazz) + clazz.allSuperclasses)
        .flatMap { it.declaredMemberProperties }
        .mapNotNull { property -> findSerializationNameAnnotation(property)?.let { property to it } }
        .associate { it.first.name to getJsonName(it.first, it.second) }
}

fun getConstructorDefaults(clazz: KClass<*>): List<Pair<Any, KCallable<*>>> {
    return generateSequence(clazz) { child -> child.superclasses.find { !it.java.isInterface } }
        .asIterable()
        .mapNotNull { it.companionObject }
        .flatMap { companion -> generateSequence { companion.objectInstance }.asIterable().zip(companion.functions) }
        .filter { it.second.hasAnnotation<ManualJsonConstructor>() }
        .onEach { it.second.isAccessible = true }
        .reversed()
}

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
            val jsonName = when {
                lowerName.endsWith("drawablename") -> name
                lowerName.endsWith("drawable") -> name + "Name"
                else -> name + "DrawableName"
            }
            json[jsonName]?.let {
                if (it.isNull) null else game.textureManager.loadDrawable(it.asString()) to true
            } ?: null to false
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

private fun getJsonType(clazz: KClass<*>): JsonType<*> {
    val constructor = clazz.primaryConstructor ?: throw IllegalArgumentException(
        "Class $clazz should have a primary constructor to be serializable"
    )

    val parameters = constructor.parameters.map { param ->
        param.name?.let { it to param.type.jvmErasure }
            ?: throw IllegalArgumentException("Parameter names should not be null")
    }.toSet()

    val parameterToJsonName = getPropertyJsonNames(clazz)
    val jsonConstructorDefaults = getConstructorDefaults(clazz)

    return JsonType(clazz.simpleName!!) { game, json ->
        val parametersFromJson = parameters
            .associate {
                it.first to getFromJson(
                    parameterToJsonName[it.first] ?: it.first, it.second, json, game!!, jsonConstructors
                )
            }
            .toMutableMap()
        jsonConstructorDefaults.forEach { it.second.call(it.first, parametersFromJson) }

        val notProvided = parametersFromJson.filter { !it.value.second }
        if (notProvided.isNotEmpty()) {
            throw IllegalArgumentException("Json is invalid, some parameters were not provided: ${notProvided.keys}")
        }
        constructor.callBy(constructor.parameters.associateWith { parametersFromJson[it.name!!]!!.first })
    }
}

private fun addNonScanningConstructors(map: MutableMap<String, Map<String, JsonType<*>>>) {
    // TODO use reflection
    val b2dType = JsonType("Box2D body type") { _, json ->
        val jsonValue = json.asString()
        Constructors.b2dTypes[jsonValue] ?: throw IllegalArgumentException(
            "Unexpected b2d type $jsonValue (allowed: ${Constructors.b2dTypes.keys})"
        )
    }

    map["bodyPattern"] = mapOf(
        "rectangleWithTopGSBottomGS" to JsonType("Rectangle body pattern with top and bottom ground sensors") { _, json ->
            createRectangleBPWithTGSBGS(
                json["b2dType", b2dType],
                json["bodyPosition", vector],
                json["bodySize", vector]
            )
        },
        "rectangleWithTopGS" to JsonType("Rectangle body pattern with top ground sensor") { _, json ->
            createRectangleBPWithTGS(
                json["b2dType", b2dType],
                json["bodyPosition", vector],
                json["bodySize", vector]
            )
        },
        "polygonWithTopGS" to JsonType("Polygon body pattern with top ground sensor") { _, json ->
            createPolygonBPWithTGS(
                json["b2dType", b2dType],
                json["worldBodyCenter", vector],
                json["localVertices", vectorArray]
            )
        },
        "circle" to JsonType("Polygon body pattern with top ground sensor") { _, json ->
            createCircleBP(json["b2dType", b2dType], json["bodyCenter", vector], json["bodyRadius", float])
        },
    )
}

val jsonConstructors: Map<String, Map<String, JsonType<*>>> by lazy {
    val result = Reflection.serializationTypeClasses
        .groupBy { it.findAnnotation<SerializationType>()!!.category }
        .mapValues { (_, classes) ->
            classes.associateBy { it.findAnnotation<SerializationType>()!!.type }
                .mapValues { getJsonType(it.value) }
        }
        .toMutableMap()
    addNonScanningConstructors(result)
    result
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
