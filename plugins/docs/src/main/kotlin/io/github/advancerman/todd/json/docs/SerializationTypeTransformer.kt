package io.github.advancerman.todd.json.docs

import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.model.Annotations
import org.jetbrains.dokka.model.ArrayValue
import org.jetbrains.dokka.model.Bound
import org.jetbrains.dokka.model.ClassValue
import org.jetbrains.dokka.model.Contravariance
import org.jetbrains.dokka.model.Covariance
import org.jetbrains.dokka.model.DClass
import org.jetbrains.dokka.model.DClasslike
import org.jetbrains.dokka.model.DEnum
import org.jetbrains.dokka.model.DFunction
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.model.DObject
import org.jetbrains.dokka.model.DPackage
import org.jetbrains.dokka.model.DParameter
import org.jetbrains.dokka.model.GenericTypeConstructor
import org.jetbrains.dokka.model.Invariance
import org.jetbrains.dokka.model.Nullable
import org.jetbrains.dokka.model.PrimaryConstructorExtra
import org.jetbrains.dokka.model.Projection
import org.jetbrains.dokka.model.Star
import org.jetbrains.dokka.model.StringValue
import org.jetbrains.dokka.model.TypeAliased
import org.jetbrains.dokka.model.TypeParameter
import org.jetbrains.dokka.model.Void
import org.jetbrains.dokka.model.WithConstructors
import org.jetbrains.dokka.model.doc.Description
import org.jetbrains.dokka.model.doc.DocumentationNode
import org.jetbrains.dokka.model.doc.Text
import org.jetbrains.dokka.model.properties.PropertyContainer
import org.jetbrains.dokka.model.properties.WithExtraProperties
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
            is DClass, is DEnum -> {
                getSerializationData((classlike as WithExtraProperties<*>).extra).map { (baseClass, type) ->
                    (classlike as WithConstructors).constructors.find {
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
                }
            }
            is DObject -> {
                classlike.functions
                    .flatMap { f ->
                        getSerializationData(f.extra).map { (baseClass, type) ->
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
            ?.params
            ?.let { params ->
                val baseClasses = (params["baseClasses"]!! as ArrayValue).value
                    .map { (it as ClassValue).className }
                if (baseClasses.isEmpty()) {
                    throw IllegalArgumentException(
                        "SerializationType annotation must have at least 1 base class"
                    )
                }
                val type = params["type"]?.let { (it as StringValue).value }
                baseClasses.map { it to (type ?: "$it.Default") }
            }
            ?: listOf()

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

    private fun Projection.withBaseTypeDri(anotherTypes: Set<String>): Projection {
        return when (this) {
            is Bound -> withBaseTypeDri(anotherTypes)
            Star -> this
            is Covariance<*> -> copy(inner.withBaseTypeDri(anotherTypes))
            is Contravariance<*> -> copy(inner.withBaseTypeDri(anotherTypes))
            is Invariance<*> -> copy(inner.withBaseTypeDri(anotherTypes))
        }
    }

    // TODO serialization primitives
    private fun Bound.withBaseTypeDri(anotherTypes: Set<String>): Bound =
        when (this) {
            is TypeAliased -> inner.withBaseTypeDri(anotherTypes)
            is Nullable -> copy(inner.withBaseTypeDri(anotherTypes))
            is TypeParameter -> this
            is GenericTypeConstructor -> {
                val dri = DRI(dri.classNames, dri.classNames)

                copy(
                    dri = if (dri.classNames in anotherTypes) dri else this.dri,
                    projections = projections.map { projection ->
                        projection.withBaseTypeDri(anotherTypes)
                    }
                )
            }
            else -> this
        }

    private fun DParameter.withBaseTypeDri(anotherTypes: Set<String>): DParameter =
        copy(type = type.withBaseTypeDri(anotherTypes))
}
