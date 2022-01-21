package io.github.advancerman.todd.json

import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class SerializationType(val baseClasses: Array<KClass<*>>, val type: String = "")
