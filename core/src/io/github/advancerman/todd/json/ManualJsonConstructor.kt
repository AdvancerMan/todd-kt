package io.github.advancerman.todd.json

@Target(AnnotationTarget.FUNCTION)
annotation class ManualJsonConstructor(val constructorName: String = "")
