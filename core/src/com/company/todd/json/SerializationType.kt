package com.company.todd.json

import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class SerializationType(val baseClass: KClass<*>, val type: String = "")
