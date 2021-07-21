package com.company.todd.util

import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.json.ManualJsonConstructor
import com.company.todd.json.SerializationType
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmName

object Reflection {
    private fun loadClass(name: String) =
        Reflection.javaClass.classLoader.loadClass(name)

    private val metadata: JsonValue by lazy {
        JsonReader().parse(
            Reflection.javaClass.classLoader
                .getResource("META-INF/my-reflections/my-reflections.json")!!
                .openStream()
        )
    }

    private val serializationTypeClasses by lazy {
        metadata["serializationTypeClasses"].map { json ->
            val clazz = loadClass(json.name).kotlin
            val serializationTypeAnnotation = clazz.findAnnotation<SerializationType>()
            val manualConstructorClass = json["manualConstructorClass"]?.asString()
                ?.let { name -> loadClass(name).kotlin }

            SerializationTypeData(
                serializationTypeAnnotation?.category,
                serializationTypeAnnotation?.type,
                manualConstructorClass?.objectInstance,
                manualConstructorClass?.let { nonNullClass ->
                    nonNullClass.declaredFunctions.find { it.hasAnnotation<ManualJsonConstructor>() }!!
                        .also { it.isAccessible = true }
                },
                (if (json.has("parametersName")) clazz.primaryConstructor!! else null)
                    ?.apply { isAccessible = true },
                clazz.jvmName,
                json["parametersName"]?.asStringArray()?.asList(),
                json["superclass"]?.asString()?.let { loadClass(it).kotlin }
            )
        }
    }

    private val serializationTypeFunctions by lazy {
        metadata["serializationTypeFunctions"].flatMap { classJson ->
            val clazz = loadClass(classJson.name).kotlin
            val constructorParameters = classJson.associate { functionJson ->
                functionJson.name to functionJson.asStringArray().asList()
            }
            val (constructors, others) = clazz.declaredFunctions.partition { it.hasAnnotation<SerializationType>() }
            val manualConstructors = others.partition { it.hasAnnotation<ManualJsonConstructor>() }.first
                .onEach { it.isAccessible = true }
                .associateBy { it.findAnnotation<ManualJsonConstructor>()!!.constructorName }
            val objectInstance = clazz.objectInstance

            constructors.map {
                it.isAccessible = true
                val serializationTypeAnnotation = it.findAnnotation<SerializationType>()!!
                SerializationTypeData(
                    serializationTypeAnnotation.category, serializationTypeAnnotation.type,
                    objectInstance, manualConstructors[it.name], it,
                    "${clazz.jvmName}::${it.name}", constructorParameters[it.name]!!, null
                )
            }
        }
    }

    val serializationTypeData by lazy {
        serializationTypeClasses + serializationTypeFunctions
    }

    data class SerializationTypeData(
        val serializationCategory: String?,
        val serializationType: String?,
        val manualConstructorInstance: Any?,
        val manualConstructor: KCallable<*>?,
        // constructor == null if class is not annotated with @SerializationType
        val constructor: KCallable<*>?,
        val constructorDescriptor: String,
        val parametersName: List<String>?,
        val superclass: KClass<*>?
    )
}
