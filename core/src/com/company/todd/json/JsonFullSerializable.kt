package com.company.todd.json

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class JsonFullSerializable(val name: String = "")
