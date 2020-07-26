package com.company.todd.objects.passive.level

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.ToddGame
import com.company.todd.launcher.assetsFolder
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.util.asset.texture.checkContains
import com.company.todd.util.asset.texture.drawable.MyDrawable
import com.company.todd.util.files.crawlJsonListsWithComments
import kotlin.reflect.KClass

const val levelsPath = "levels/"

private val stringInfos = PassiveObjectInfo.values().map { it.toString() }

fun loadLevels() =
        crawlJsonListsWithComments(assetsFolder + levelsPath).map { json ->
            checkContains(json, "name", "string") { it.isString }
            checkContains(json, "objects", "array of level objects") { it.isArray }
            Level(json["name"].asString(), json["objects"].map { jsonToObjectInfo(it) }.toMutableList())
        }

private fun jsonToObjectInfo(objectJson: JsonValue): (ToddGame) -> PassiveObject {
    checkContains(objectJson, "type", "one of strings: $stringInfos") {
        it.isString && stringInfos.contains(it.asString())
    }
    val info = PassiveObjectInfo.valueOf(objectJson["type"].asString())
    checkContains(objectJson, "args", "array of $info arguments") { it.isArray }
    val argsJson = objectJson["args"]

    return { game ->
        info.constructor.call(
                *info
                        .constructor
                        .parameters
                        .map { parameter ->
                            require(parameter.type.classifier is KClass<*>) {
                                "${parameter.type.classifier} should be KClass " +
                                        "(for $info constructor)"
                            }

                            when (val type = parameter.type.classifier as KClass<*>) {
                                ToddGame::class -> game
                                MyDrawable::class -> game.textureManager.loadSprite(info.drawableName)
                                else -> jsonToObject(type, argsJson[parameter.index - 2])
                            }
                        }
                        .toTypedArray()
        )
    }
}

private enum class JsonType(val clazz: KClass<out Any>, val fromJson: (JsonValue) -> Any) {
    FLOAT(Float::class, { it.asFloat() }),
    VECTOR(Vector2::class, { json -> json.asFloatArray().let { Vector2(it[0], it[1]) } }),
    RECTANGLE(Rectangle::class, { json -> json.asFloatArray().let { Rectangle(it[0], it[1], it[2], it[3]) } }),
    VECTOR_ARRAY(Array<Vector2>::class, { json -> json.map { VECTOR.fromJson(it) as Vector2 }.toTypedArray() })
}

private val argumentConstructors = JsonType.values().associateBy { it.clazz }

private fun jsonToObject(clazz: KClass<*>, json: JsonValue): Any =
        argumentConstructors
                .getOrElse(clazz) { error("$argumentConstructors should contain $clazz as key") }
                .fromJson(json)
