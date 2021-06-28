package com.company.todd.json

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class JsonSaveSerializable(val name: String = "")
