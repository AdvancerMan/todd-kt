package com.company.todd.objects.passive.level

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.ToddGame
import com.company.todd.launcher.assetsFolder
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.objects.passive.interactive.Jumper
import com.company.todd.objects.passive.interactive.Trampoline
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.objects.passive.platform.SolidPolygonPlatform
import com.company.todd.objects.passive.platform.SolidRectanglePlatform
import com.company.todd.util.asset.texture.checkContains
import com.company.todd.util.files.crawlJsonListsWithComments
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility

private fun <T : PassiveObject> getPassiveConstructor(clazz: KClass<T>) : KFunction<T> {
    require(clazz.constructors.count { it.visibility == KVisibility.PUBLIC } == 1) {
        "To use ${clazz.simpleName} in level should have only 1 public constructor"
    }
    return clazz.constructors.first { it.visibility == KVisibility.PUBLIC }
}

enum class PassiveObjectInfo(val constructor: KFunction<PassiveObject>) {
    SOLID_RECTANGLE_PLATFORM(getPassiveConstructor(SolidRectanglePlatform::class)),
    SOLID_POLYGON_PLATFORM(getPassiveConstructor(SolidPolygonPlatform::class)),
    HALF_COLLIDED_PLATFORM(getPassiveConstructor(HalfCollidedPlatform::class)),
    JUMPER(getPassiveConstructor(Jumper::class)),
    TRAMPOLINE(getPassiveConstructor(Trampoline::class))
}

const val levelsPath = "levels/"

private val stringInfos = PassiveObjectInfo.values().map { it.toString() }

fun loadLevels() =
        crawlJsonListsWithComments(assetsFolder + levelsPath).map { json ->
            checkContains(json, "name", "string") { it.isString }
            checkContains(json, "objects", "array of level objects") { it.isArray }
            Level(json["name"].asString(), json["objects"].map { jsonToObjectInfo(it) }.toMutableList())
        }

private fun jsonToObjectInfo(json: JsonValue): (ToddGame) -> PassiveObject {
    checkContains(json, "type", "one of strings: $stringInfos") {
        it.isString && stringInfos.contains(it.asString())
    }

    return { game ->
        PassiveObjectInfo
                .valueOf(json["type"].asString())
                .constructor.let { constructor ->
                    constructor.call(game,
                            *constructor.parameters
                                    .map {
                                        require(it.type.classifier is KClass<*>) {
                                            "${it.type.classifier} should be KClass " +
                                                    "(for ${PassiveObjectInfo.valueOf(json["type"].asString())} constructor)"
                                        }
                                        // TODO parse List<Rectangle> etc
                                        jsonToObject(game, it.type.classifier as KClass<*>, json[it.index + 1])
                                    }
                                    .toTypedArray()
                    )
                }
    }
}

private enum class JsonType(val clazz: KClass<out Any>, val fromJson: (JsonValue) -> Any) {
    INT(Int::class, { it.asInt() }),
    LONG(Long::class, { it.asLong() }),
    FLOAT(Float::class, { it.asFloat() }),
    DOUBLE(Double::class, { it.asDouble() }),
    STRING(String::class, { it.asString() }),
    BOOLEAN(Boolean::class, { it.asBoolean() }),
    INT_ARRAY(IntArray::class, { it.asIntArray() }),
    FLOAT_ARRAY(FloatArray::class, { it.asFloatArray() }),
    STRING_ARRAY(Array<String>::class, { it.asStringArray() })
}

private val argumentConstructors: Map<KClass<*>, Array<JsonType>> = mapOf(
        Rectangle::class to arrayOf(JsonType.FLOAT, JsonType.FLOAT, JsonType.FLOAT, JsonType.FLOAT),
        Vector2::class to arrayOf(JsonType.FLOAT, JsonType.FLOAT),
        Float::class to arrayOf(JsonType.FLOAT)
)

private fun <T : Any> jsonToObject(game: ToddGame, clazz: KClass<T>, json: JsonValue): T =
        argumentConstructors
                .getOrElse(clazz) { error("$argumentConstructors should contain $clazz as key") }
                .let { types ->
                    clazz.java
                            .getConstructor(*types.map { it.clazz.java }.toTypedArray())
                            .newInstance(types.mapIndexed { i, it -> it.fromJson(json[i + 1]) })
                }
