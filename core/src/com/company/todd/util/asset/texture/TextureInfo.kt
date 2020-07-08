package com.company.todd.util.asset.texture

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.assetsFolder
import com.company.todd.util.files.crawlJsonListsWithComments

data class RegionInfo(val path: String, val x: Int, val y: Int, val w: Int, val h: Int)
data class AnimationInfo(val path: String, val frameDuration: Float, val bounds: List<Rectangle>)
data class AnimationPackInfo(val animations: List<Pair<AnimationType, AnimationInfo>>)

const val texturesPath = "pics/"

fun loadTextureInfos(): Triple<
        Map<String, RegionInfo>,
        Map<String, AnimationInfo>,
        Map<String, AnimationPackInfo>
        > {
    val json = crawlJsonListsWithComments(assetsFolder + texturesPath)
    val reg = mutableMapOf<String, RegionInfo>()
    val anim = mutableMapOf<String, AnimationInfo>()
    val anims = mutableMapOf<String, AnimationPackInfo>()

    json.forEach {
        checkContains(it, "type", "reg, anim or anims") { json ->
            json.isString && json.asString() in listOf("reg", "anim", "anims")
        }
        when(it["type"].asString()) {
            "reg" -> {
                checkReg(it, reg)
                val xywh = it["xywh"].asIntArray()
                reg[it["name"].asString()] = RegionInfo(
                        texturesPath + it["path"].asString(),
                        xywh[0], xywh[1], xywh[2], xywh[3]
                )
            }

            "anim" -> {
                checkAnim(it, anim)
                anim[it["name"].asString()] = parseAnimInfo(it)
            }

            "anims" -> {
                checkAnims(it, anims)
                anims[it["name"].asString()] = AnimationPackInfo(
                        it["anims"].map { anim ->
                            AnimationType.valueOf(anim.name) to parseAnimInfo(anim)
                        }
                )
            }
        }
    }

    return Triple(reg, anim, anims)
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
            json["frameDuration"].asFloat() / 1000, bounds
    )
}

private fun checkContains(json: JsonValue, key: String, shouldBe: String, checker: (JsonValue) -> Boolean) {
    val value = json[key] ?: throw IllegalArgumentException("Json should contain $key, json: $json")
    require(checker(value)) { "$key should be $shouldBe, json: $json" }
}

private fun <T> checkName(json: JsonValue, map: Map<String, T>) {
    checkContains(json, "name", "String (probably this name was already used)") {
        it.isString && !map.containsKey(it.asString())
    }
}

private fun checkRectangle(json: JsonValue) =
        json.isArray && json.size == 4 && !json.any { it.type() != JsonValue.ValueType.longValue }

private fun checkReg(json: JsonValue, reg: Map<String, RegionInfo>) {
    checkContains(json, "path", "String") { it.isString }
    checkContains(json, "xywh", "Int Array") { checkRectangle(it) }
    checkName(json, reg)
}

private fun checkAnim(json: JsonValue, anim: Map<String, AnimationInfo>?) {
    checkContains(json, "path", "String") { it.isString }
    checkContains(json, "frameDuration", "Float") { it.isDouble || it.isLong }

    if (json.has("bounds")) {
        checkContains(json, "bounds", "2D Int Array") {
            it.isArray && !it.any { e -> !checkRectangle(e) }
        }
    } else {
        checkContains(json, "xywh", "Int Array") { checkRectangle(it) }
        checkContains(json, "r", "Integer") { it.isLong }
        checkContains(json, "c", "Integer") { it.isLong }
    }

    if (anim != null) {
        checkName(json, anim)
    }
}

val animTypes = AnimationType.values().toList().map { it.toString() }

private fun checkAnims(json: JsonValue, anims: Map<String, AnimationPackInfo>) {
    checkContains(json, "anims", "map, keys are $animTypes") {
        it.isObject && !it.any { json -> !animTypes.contains(json.name) }
    }
    json["anims"].forEach { checkAnim(it, null) }
    checkName(json, anims)
}
