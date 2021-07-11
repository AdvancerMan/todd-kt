package com.company.todd.json

@Target(AnnotationTarget.CLASS)
annotation class SerializationType(val category: String, val type: String = "")
