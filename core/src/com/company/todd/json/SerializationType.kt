package com.company.todd.json

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class SerializationType(val category: String, val type: String = "")
