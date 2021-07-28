package com.company.todd.json.docs

import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.model.*
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
                    classes.value.map {
                        it.copy(
                            constructors = listOf(
                                it.constructors[0].toJsonConstructor(map, it.name)
                            )
                        )
                    }
                }
            }
            .map {
                DPackage(
                    dri = DRI(it.key),
                    functions = listOf(),
                    properties = listOf(),
                    classlikes = it.value,
                    typealiases = listOf(),
                    documentation = mapOf(),
                    expectPresentInSet = null,
                    sourceSets = original.sourceSets
                )
            }
            .let { original.copy(packages = it) }
    }

    private fun classlikesDfs(roots: List<DClasslike>) : MutableList<DClasslike> {
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
                getSerializationData(classlike.extra)?.let { (category, type) ->
                    listOf(
                        classlike.copy(
                            dri = DRI(category, type),
                            name = type,
                            constructors = listOf(
                                classlike.constructors.find {
                                    it.extra.allOfType<PrimaryConstructorExtra>()
                                        .isNotEmpty()
                                } ?: throw IllegalArgumentException(
                                    "Serialization type class should have primary constructor"
                                )
                            ),
                            functions = listOf(),
                            properties = listOf(),
                            classlikes = listOf(),
                            companion = null,
                            generics = listOf(),
                            supertypes = mapOf(),
                            extra = PropertyContainer.empty()
                        )
                    )
                } ?: listOf()
            }
            is DObject -> {
                classlike.functions
                    .mapNotNull { f ->
                        getSerializationData(f.extra)?.let { (category, type) ->
                            DClass(
                                dri = DRI(category, type),
                                name = type,
                                constructors = listOf(f),
                                functions = listOf(),
                                properties = listOf(),
                                classlikes = listOf(),
                                companion = null,
                                generics = listOf(),
                                supertypes = mapOf(),
                                sources = f.sources,
                                visibility = f.visibility,
                                documentation = f.documentation,
                                expectPresentInSet = f.expectPresentInSet,
                                modifier = f.modifier,
                                sourceSets = f.sourceSets,
                                isExpectActual = f.isExpectActual
                            )
                        }
                    }
            }
            else -> listOf()
        }

    private fun getSerializationData(extra: PropertyContainer<*>) =
        extra.allOfType<Annotations>()
            .flatMap { anns ->
                anns.directAnnotations.values
            }
            .flatten()
            .find {
                it.dri.classNames == "SerializationType"
            }
            ?.params?.let { params ->
                (params["category"]!! as StringValue).value to
                        (params["type"]?.let { (it as StringValue).value } ?: "Default")
            }

    private fun DFunction.toJsonConstructor(
        anotherTypes: Map<String, List<DClass>>,
        type: String
    ): DFunction {
        return copy(
            // TODO parameters
            name = type,
            isConstructor = true,
            extra = PropertyContainer.withAll(
                PrimaryConstructorExtra
            )
        )
    }
}
