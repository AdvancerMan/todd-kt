package com.company.todd.util.asset.texture

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.assetsFolder
import com.company.todd.util.files.crawlJsonListsWithComments

data class RegionInfo(val path: String, val x: Float, val y: Float, val w: Float, val h: Float)
data class AnimationInfo(val path: String, val frameDuration: Float, val bounds: List<Rectangle>)
data class AnimationPackInfo(val animations: List<Pair<AnimationType, AnimationInfo>>)

const val texturesPath = "pics"

fun loadTextureInfos(): Triple<
        Map<String, RegionInfo>,
        Map<String, AnimationInfo>,
        Map<String, AnimationPackInfo>
        > {
    val json = crawlJsonListsWithComments(assetsFolder)
    val reg = mutableMapOf<String, RegionInfo>()
    val anim = mutableMapOf<String, AnimationInfo>()
    val anims = mutableMapOf<String, AnimationPackInfo>()

    json.forEach {
        when(it["type"].asString()) {
            "reg" -> {
                val xywh = it["xywh"].asFloatArray()
                reg[it["name"].asString()] = RegionInfo(
                        it["path"].asString(),
                        xywh[0], xywh[1], xywh[2], xywh[3]
                )
            }

            "anim" -> {
                anim[it["name"].asString()] = parseAnimInfo(it)
            }

            "anims" -> {
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
                    val regBounds = it.asFloatArray()
                    Rectangle(regBounds[0], regBounds[1], regBounds[2], regBounds[3])
                }
            } else {
                val c = json["c"].asInt()
                val r = json["r"].asInt()
                val xywh = json["xywh"].asFloatArray()
                val x = xywh[0]
                val y = xywh[1]
                val dx = xywh[2] / c
                val dy = xywh[3] / r

                List(r * c) {
                    Rectangle(x + it % c * dx, y + it / c * dy, dx, dy)
                }
            }

    return AnimationInfo(json["path"].asString(), json["frameDuration"].asFloat() / 1000, bounds)
}
