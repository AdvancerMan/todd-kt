package com.company.todd.json.serialization

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.JsonUpdateSerializable
import com.company.todd.json.ManuallyJsonSerializable
import com.company.todd.json.SerializationType
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

fun getJsonName(member: KCallable<*>, annotation: Annotation) =
    when (annotation) {
        is JsonFullSerializable -> if (annotation.name.isBlank()) member.name else annotation.name
        is JsonUpdateSerializable -> if (annotation.name.isBlank()) member.name else annotation.name
        else -> member.name
    }

private fun toJson(obj: Any?, vararg annotations: KClass<*>): JsonValue {
    return when (obj) {
        null -> JsonValue(JsonValue.ValueType.nullValue)
        is String -> obj.toJsonValue()
        is Vector2 -> obj.toJsonValue()
        is Rectangle -> obj.toJsonValue()
        is Boolean -> obj.toJsonValue()
        is Float -> obj.toJsonValue()
        else -> {
            val result = JsonValue(JsonValue.ValueType.`object`)
            val clazz = obj::class
            clazz.findAnnotation<SerializationType>()?.let { result.addChild("type", it.type.toJsonValue()) }
            val classes = listOf(listOf(clazz), clazz.allSuperclasses).flatten()
            classes.flatMap { it.declaredMembers }
                .map { member -> member to member.annotations.find { annotation -> annotations.any { annotation::class.isSubclassOf(it) } } }
                .filter { it.second != null }
                .onEach { it.first.isAccessible = true }
                 // expecting getters only
                .associate { getJsonName(it.first, it.second!!) to toJson(it.first.call(obj), *annotations) }
                .forEach { result.addChild(it.key, it.value) }

            if (obj is ManuallyJsonSerializable) {
                if (JsonFullSerializable::class in annotations) {
                    obj.serializeFull(result)
                }
                obj.serializeUpdates(result)
            }

            result
        }
    }
}

fun Any.toJsonFull() = toJson(this, JsonUpdateSerializable::class, JsonFullSerializable::class)

fun Any.toJsonUpdates() = toJson(this, JsonUpdateSerializable::class)
