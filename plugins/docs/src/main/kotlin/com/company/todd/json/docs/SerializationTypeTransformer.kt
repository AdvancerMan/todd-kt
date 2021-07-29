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
                                it.constructors[0].toJsonConstructor(map.keys, it.name)
                            )
                        )
                    }
                }
            }
            .map {
                DPackage(
                    dri = DRI(it.key, it.key),
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
                getSerializationData(classlike.extra)?.let { (baseClass, type) ->
                    listOf(
                        classlike.copy(
                            dri = DRI(baseClass, type),
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
                            visibility = mapOf(),
                            expectPresentInSet = null,
                            modifier = mapOf(),
                            isExpectActual = false,
                            extra = PropertyContainer.empty()
                        )
                    )
                } ?: listOf()
            }
            is DObject -> {
                classlike.functions
                    .mapNotNull { f ->
                        getSerializationData(f.extra)?.let { (baseClass, type) ->
                            DClass(
                                dri = DRI(baseClass, type),
                                name = type,
                                constructors = listOf(f),
                                functions = listOf(),
                                properties = listOf(),
                                classlikes = listOf(),
                                companion = null,
                                generics = listOf(),
                                supertypes = mapOf(),
                                sources = f.sources,
                                visibility = mapOf(),
                                documentation = f.documentation,
                                expectPresentInSet = null,
                                modifier = mapOf(),
                                sourceSets = f.sourceSets,
                                isExpectActual = false
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
                (params["baseClass"]!! as ClassValue).className to
                        (params["type"]?.let { (it as StringValue).value } ?: "Default")
            }

    private fun DFunction.toJsonConstructor(anotherTypes: Set<String>, type: String): DFunction =
        copy(
            name = type,
            isConstructor = true,
            parameters = parameters
                .filter { it.name != null }
                .map { it.withBaseTypeDri(anotherTypes) }
                .filter {
                    it.type !is GenericTypeConstructor
                            || (it.type as GenericTypeConstructor).dri.classNames != "ToddGame"
                },
            extra = PropertyContainer.withAll(
                PrimaryConstructorExtra
            ),
        )

    // TODO serialization primitives
    private fun Bound.withBaseTypeDri(anotherTypes: Set<String>): Bound =
        when (this) {
            is TypeAliased -> inner.withBaseTypeDri(anotherTypes)
            is Nullable -> copy(inner.withBaseTypeDri(anotherTypes))
            is TypeParameter, is GenericTypeConstructor -> {
                val dri = when(this) {
                    is TypeParameter -> dri
                    is GenericTypeConstructor -> dri
                    else -> throw AssertionError("Never happens")
                }.let { DRI(it.classNames, it.classNames) }

                if (dri.classNames in anotherTypes) {
                    when(this) {
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
