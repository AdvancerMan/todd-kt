package com.company.todd.json

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class SerializationType(val category: String, val type: String = "")
