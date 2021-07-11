package com.company.todd.util

import org.reflections.Reflections
import kotlin.reflect.KClass

object Reflection {
    fun getAllClassesWithAnnotation(annotation: KClass<out Annotation>): List<KClass<*>> =
        Reflections("com.company.todd")
            .getTypesAnnotatedWith(annotation.java)
            .map { it.kotlin }
            .filter { clazz -> clazz.annotations.any { it.annotationClass == annotation } }
}
