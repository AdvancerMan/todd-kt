package com.company.todd.util

import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import kotlin.reflect.KClass

object Reflection {
    private val metadata: JsonValue by lazy {
        JsonReader().parse(
            Reflection.javaClass.classLoader
                .getResource("META-INF/my-reflections/my-reflections.json")!!
                .openStream()
        )
    }

    fun getAllClassesWithSerializationTypeAnnotation(): List<KClass<*>> =
        metadata["annotatedWithSerializationType"].map {
            Class.forName(it.asString()).kotlin
        }
}
