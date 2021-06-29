package com.company.todd.json.serialization

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.json.*
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

fun getJsonName(member: KCallable<*>, annotation: Annotation): String {
    val annotationName = when (annotation) {
        is JsonSaveSerializable -> annotation.name
        is JsonFullSerializable -> annotation.name
        is JsonUpdateSerializable -> annotation.name
        else -> ""
    }
    return if (annotationName.isBlank()) member.name else annotationName
}

private fun toJson(obj: Any?, vararg annotations: KClass<*>): JsonValue {
    return when (obj) {
        null -> JsonValue(JsonValue.ValueType.nullValue)
        is Int -> obj.toJsonValue()
        is Long -> obj.toJsonValue()
        is String -> obj.toJsonValue()
        is Vector2 -> obj.toJsonValue()
        is Rectangle -> obj.toJsonValue()
        is Boolean -> obj.toJsonValue()
        is Float -> obj.toJsonValue()
        is Enum<*> -> obj.name.toJsonValue()
        is List<*> -> {
            val result = JsonValue(JsonValue.ValueType.array)
            obj.map { toJson(it, *annotations) }.forEach { result.addChild(it) }
            result
        }
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
                if (JsonSaveSerializable::class in annotations) {
                    obj.serializeSave(result)
                }
                if (JsonFullSerializable::class in annotations) {
                    obj.serializeFull(result)
                }
                obj.serializeUpdates(result)
            }

            result
        }
    }
}

fun Any.toJsonSave() =
    toJson(this, JsonUpdateSerializable::class, JsonFullSerializable::class, JsonSaveSerializable::class)

fun Any.toJsonFull() =
    toJson(this, JsonUpdateSerializable::class, JsonFullSerializable::class)

fun Any.toJsonUpdates() =
    toJson(this, JsonUpdateSerializable::class)
