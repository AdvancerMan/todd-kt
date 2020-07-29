package com.company.todd.util.asset.texture

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.assetsFolder
import com.company.todd.util.asset.texture.RegionInfoTypes.*
import com.company.todd.util.asset.texture.animated.AnimationType
import com.company.todd.util.files.crawlJsonListsWithComments

interface TextureInfo

enum class RegionInfoTypes {
    REGION, TILED, COVERED_TILED, NINE_TILED
}

open class RegionInfo(val path: String, val x: Int, val y: Int, val w: Int, val h: Int) : TextureInfo

class TiledRegionInfo(path: String, x: Int, y: Int, w: Int, h: Int) : RegionInfo(path, x, y, w, h)

class NineTiledRegionInfo(
        path: String, x: Int, y: Int, w: Int, h: Int,
        val lw: Int, val rw: Int, val uh: Int, val dh: Int
) : RegionInfo(path, x, y, w, h)

class CoveredTiledRegionInfo(
        path: String, x: Int, y: Int,
        w: Int, h: Int, val uh: Int
) : RegionInfo(path, x, y, w, h)

data class AnimationInfo(
        val path: String, val frameDuration: Float,
        val mode: Animation.PlayMode, val bounds: List<Rectangle>
) : TextureInfo

data class AnimationPackInfo(val animations: List<Pair<AnimationType, AnimationInfo>>) : TextureInfo

const val texturesPath = "pics/"

// TODO load NineTiledRegionInfo and CoveredTiledRegionInfo
fun loadTextureInfos(): Map<String, TextureInfo> {
    val res = mutableMapOf<String, TextureInfo>()

    crawlJsonListsWithComments(assetsFolder + texturesPath).forEach { json ->
        checkContains(json, "type", "reg, anim or anims") {
            it.isString && it.asString() in listOf("reg", "anim", "anims")
        }
        checkName(json, res.keys)

        when (json["type"].asString()) {
            "reg" -> {
                checkReg(json)
                val xywh = json["xywh"].asIntArray()
                res[json["name"].asString()] = parseRegInfo(
                        json, texturesPath + json["path"].asString(),
                        xywh[0], xywh[1], xywh[2], xywh[3]
                )
            }

            "anim" -> {
                checkAnim(json)
                res[json["name"].asString()] = parseAnimInfo(json)
            }

            "anims" -> {
                checkAnims(json)
                res[json["name"].asString()] = AnimationPackInfo(
                        json["anims"].map {
                            AnimationType.valueOf(it.name) to parseAnimInfo(it)
                        }
                )
            }
        }
    }

    return res
}

private fun parseRegInfo(json: JsonValue, path: String, x: Int, y: Int, w: Int, h: Int) =
        when (if (json.has("regType")) valueOf(json["regType"].asString()) else REGION) {
            REGION -> RegionInfo(path, x, y, w, h)
            TILED -> TiledRegionInfo(path, x, y, w, h)
            COVERED_TILED -> CoveredTiledRegionInfo(path, x, y, w, h, json["uh"].asInt())
            NINE_TILED -> json["lrud"].asIntArray().let {
                NineTiledRegionInfo(path, x, y, w, h, it[0], it[1], it[2], it[3])
            }
        }

private fun parseAnimInfo(json: JsonValue): AnimationInfo {
    val bounds =
            if (json.has("bounds")) {
                json["bounds"].map {
                    val regBounds = it.asIntArray()
                    Rectangle(
                            regBounds[0].toFloat(), regBounds[1].toFloat(),
                            regBounds[2].toFloat(), regBounds[3].toFloat()
                    )
                }
            } else {
                val c = json["c"].asInt()
                val r = json["r"].asInt()
                val xywh = json["xywh"].asIntArray()
                val x = xywh[0]
                val y = xywh[1]
                val dx = xywh[2] / c
                val dy = xywh[3] / r

                List(r * c) {
                    Rectangle(
                            (x + it % c * dx).toFloat(),
                            (y + it / c * dy).toFloat(),
                            dx.toFloat(), dy.toFloat()
                    )
                }
            }

    return AnimationInfo(
            texturesPath + json["path"].asString(),
            json["frameDuration"].asFloat() / 1000,
            if (json.has("mode")) Animation.PlayMode.valueOf(json["mode"].asString())
            else Animation.PlayMode.NORMAL,
            bounds
    )
}

fun getJsonErrorMessage(json: JsonValue, message: String) = "$message, json: $json"

fun checkContains(json: JsonValue, key: String, shouldBe: String, checker: (JsonValue) -> Boolean) {
    val value = json[key]
            ?: throw IllegalArgumentException(getJsonErrorMessage(json, "Json should contain $key"))

    require(checker(value)) { "$key should be $shouldBe, json: $json" }
}

fun checkName(json: JsonValue, set: Set<String>) {
    checkContains(json, "name", "String (probably this name was already used)") {
        it.isString && !set.contains(it.asString())
    }
}

fun checkIntRectangle(json: JsonValue) =
        json.isArray && json.size == 4 && !json.any { it.type() != JsonValue.ValueType.longValue }

private fun checkReg(json: JsonValue) {
    checkContains(json, "path", "String") { it.isString }
    checkContains(json, "xywh", "Int Array") { checkIntRectangle(it) }
    if (json.has("regType")) {
        checkContains(json, "regType", "element of ${values().map { info -> info.toString() }}") {
            it.isString && it.asString() in values().map { info -> info.toString() }
        }
        when (valueOf(json["regType"].asString())) {
            COVERED_TILED -> {
                checkContains(json, "uh", "Integer") {
                    it.isLong
                }
            }
            NINE_TILED -> {
                checkContains(json, "lrud", "4 element integer array") {
                    checkIntRectangle(it)
                }
            }
            TILED -> {}
            REGION -> {}
        }
    }
}

val playModes = Animation.PlayMode.values().toList().map { it.toString() }

private fun checkAnim(json: JsonValue) {
    checkContains(json, "path", "String") { it.isString }
    checkContains(json, "frameDuration", "Float") { it.isDouble || it.isLong }
    require(!json.has("mode") || (json["mode"].isString && playModes.contains(json["mode"].asString()))) {
        getJsonErrorMessage(json, "mode should be one of strings $playModes")
    }

    if (json.has("bounds")) {
        checkContains(json, "bounds", "2D Int Array") {
            it.isArray && !it.any { e -> !checkIntRectangle(e) }
        }
    } else {
        checkContains(json, "xywh", "Int Array") { checkIntRectangle(it) }
        checkContains(json, "r", "Integer") { it.isLong }
        checkContains(json, "c", "Integer") { it.isLong }
    }
}

val animTypes = AnimationType.values().toList().map { it.toString() }

private fun checkAnims(json: JsonValue) {
    checkContains(json, "anims", "non-empty map, keys are $animTypes") {
        it.isObject && !it.any { json -> !animTypes.contains(json.name) } && it.notEmpty()
    }
    json["anims"].forEach { checkAnim(it) }
}
