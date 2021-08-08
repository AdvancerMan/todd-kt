package io.github.advancerman.todd.json

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class JsonFullSerializable(val name: String = "")
