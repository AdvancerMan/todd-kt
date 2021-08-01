package com.company.todd.json.docs

import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.model.*
import org.jetbrains.dokka.model.doc.Description
import org.jetbrains.dokka.model.doc.DocumentationNode
import org.jetbrains.dokka.model.doc.Text
import org.jetbrains.dokka.model.properties.PropertyContainer
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.transformers.documentation.DocumentableTransformer

class SerializationTypeTransformer(val context: DokkaContext) : DocumentableTransformer {
    override fun invoke(original: DModule, context: DokkaContext): DModule {
        return original.packages
            .flatMap { p ->
                classlikesDfs(p.classlikes).flatMap { c ->
                    getSerializationTypes(c)
                }
            }
            .groupBy { it.dri.packageName!! }
            .let { map ->
                map.mapValues { classes ->
                    classes.value.map { it.toJsonConstructor(map.keys) }
                }
            }
            .map {
                DPackage(
                    dri = DRI(it.key, it.key),
                    functions = it.value,
                    properties = listOf(),
                    classlikes = listOf(),
                    typealiases = listOf(),
                    documentation = mapOf(),
                    expectPresentInSet = null,
                    sourceSets = original.sourceSets
                )
            }
            .let { original.copy(packages = it) }
    }

    private fun classlikesDfs(roots: List<DClasslike>): MutableList<DClasslike> {
        if (roots.isEmpty()) {
            return mutableListOf()
        }
        return classlikesDfs(roots.flatMap { it.classlikes }).apply {
            addAll(roots)
        }
    }

    private fun getSerializationTypes(classlike: DClasslike) =
        when (classlike) {
            is DClass -> {
                getSerializationData(classlike.extra)?.let { (baseClass, type) ->
                    listOf(
                        classlike.constructors.find {
                            it.extra.allOfType<PrimaryConstructorExtra>().isNotEmpty()
                        }?.let { function ->
                            function.copy(
                                dri = DRI(baseClass, type),
                                documentation = classlike.documentation,
                                parameters = function.parameters.map { parameter ->
                                    classlike.properties
                                        .find { parameter.name == it.name }
                                        ?.let { property ->
                                            getJsonPropertyName(property.extra)
                                                ?.let { parameter.copy(name = it) }
                                        }
                                        ?: parameter
                                }
                            )
                        } ?: throw IllegalArgumentException(
                            "Serialization type class should have primary constructor"
                        )
                    )
                } ?: listOf()
            }
            is DObject -> {
                classlike.functions
                    .mapNotNull { f ->
                        getSerializationData(f.extra)?.let { (baseClass, type) ->
                            f.copy(dri = DRI(baseClass, type))
                        }
                    }
            }
            else -> listOf()
        }

    private fun anyAnnotationFrom(extra: PropertyContainer<*>, annotationNames: List<String>) =
        extra.allOfType<Annotations>()
            .flatMap { anns -> anns.directAnnotations.values }
            .flatten()
            .find { it.dri.classNames in annotationNames }

    private fun getSerializationData(extra: PropertyContainer<*>) =
        anyAnnotationFrom(extra, listOf("SerializationType"))
            ?.params?.let { params ->
                (params["baseClass"]!! as ClassValue).className to
                        (params["type"]?.let { (it as StringValue).value } ?: "Default")
            }

    private fun getJsonPropertyName(extra: PropertyContainer<*>) =
        anyAnnotationFrom(
            extra,
            listOf("JsonUpdateSerializable", "JsonFullSerializable", "JsonSaveSerializable")
        )?.params?.let { (it["name"] as? StringValue)?.value }

    private fun DFunction.toJsonConstructor(anotherTypes: Set<String>): DFunction =
        copy(
            name = dri.classNames!!,
            isConstructor = true,
            parameters = parameters
                .filter { it.name != null }
                .map { it.withBaseTypeDri(anotherTypes) }
                .filter {
                    it.type !is GenericTypeConstructor
                            || (it.type as GenericTypeConstructor).dri.classNames != "ToddGame"
                },
            expectPresentInSet = null,
            visibility = mapOf(),
            documentation = documentation.ifEmpty {
                sourceSets.associateWith {
                    DocumentationNode(listOf(Description(Text("No documentation :("))))
                }
            },
            type = Void,
            generics = listOf(),
            receiver = null,
            modifier = mapOf(),
            isExpectActual = false,
            extra = PropertyContainer.withAll(
                PrimaryConstructorExtra
            )
        )

    // TODO serialization primitives
    private fun Bound.withBaseTypeDri(anotherTypes: Set<String>): Bound =
        when (this) {
            is TypeAliased -> inner.withBaseTypeDri(anotherTypes)
            is Nullable -> copy(inner.withBaseTypeDri(anotherTypes))
            is TypeParameter, is GenericTypeConstructor -> {
                val dri = when (this) {
                    is TypeParameter -> dri
                    is GenericTypeConstructor -> dri
                    else -> throw AssertionError("Never happens")
                }.let { DRI(it.classNames, it.classNames) }

                if (dri.classNames in anotherTypes) {
                    when (this) {
                        is TypeParameter -> copy(dri = dri)
                        is GenericTypeConstructor -> copy(dri = dri)
                        else -> throw AssertionError("Never happens")
                    }
                } else {
                    this
                }
            }
            else -> this
        }

    private fun DParameter.withBaseTypeDri(anotherTypes: Set<String>): DParameter =
        copy(type = type.withBaseTypeDri(anotherTypes))
}
