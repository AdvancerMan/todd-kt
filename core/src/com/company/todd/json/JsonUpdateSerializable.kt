package com.company.todd.json

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class JsonUpdateSerializable(val name: String = "")
