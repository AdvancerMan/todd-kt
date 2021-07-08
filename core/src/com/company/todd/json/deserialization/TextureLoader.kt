package com.company.todd.json.deserialization

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.asset.texture.*
import com.company.todd.asset.texture.animated.AnimationType
import com.company.todd.util.TEXTURES_PATH
import com.company.todd.util.files.crawlJsonListsWithComments
import com.company.todd.util.files.toOsDependentPath

private val animationPackInfo = JsonType("non-empty map, keys are ${AnimationType.values().contentToString()}, " +
        "values are infos of \"anim\" type") { _, json ->
    AnimationPackInfo(json.map { AnimationType.valueOf(it.name) to parseAnimInfo(it) })
}

private val textureInfoConstructors = mapOf(
        "reg" to JsonType("texture region info") { _, json ->
            val xywh = json["xywh", intRectangle]
            parseRegInfo(
                    json, TEXTURES_PATH + json["path", string],
                    xywh.x.toInt(), xywh.y.toInt(), xywh.width.toInt(), xywh.height.toInt()
            )
        },

        "anim" to JsonType("animation info") { _, json -> parseAnimInfo(json) },
        "anims" to JsonType("animation pack info") { _, json -> json["anims", animationPackInfo] }
)

fun loadTextureInfos(): Map<String, TextureInfo> {
    val res = mutableMapOf<String, TextureInfo>()

    crawlJsonListsWithComments(TEXTURES_PATH).forEach { json ->
        checkName(json, res.keys)
        res[json["name", string]] = parseJsonValue(null, json, textureInfoConstructors)
    }

    return res
}

private val regionInfoType = JsonType(
        "region info type, one of strings: ${RegionInfoType.values().contentToString()}"
) { _, json ->
    RegionInfoType.valueOf(json.asString())
}

private fun parseRegInfo(json: JsonValue, unixPath: String, x: Int, y: Int, w: Int, h: Int) : RegionInfo {
    val path = unixPath.toOsDependentPath()
    return when (json["regType", regionInfoType, null, RegionInfoType.REGION]) {
        RegionInfoType.REGION -> RegionInfo(path, x, y, w, h)
        RegionInfoType.TILED -> TiledRegionInfo(path, x, y, w, h)
        RegionInfoType.COVERED_TILED -> CoveredTiledRegionInfo(path, x, y, w, h, json["uh", int])
        RegionInfoType.NINE_TILED -> json["lrud", intRectangle].let {
            NineTiledRegionInfo(path, x, y, w, h, it.x.toInt(), it.y.toInt(), it.width.toInt(), it.height.toInt())
        }
    }
}

private val animationPlayMode = JsonType(
        "animation play mode, one of strings: ${Animation.PlayMode.values().contentToString()}"
) { _, json ->
    Animation.PlayMode.valueOf(json.asString())
}

private fun parseAnimInfo(json: JsonValue): AnimationInfo {
    val bounds =
            if (json.has("bounds")) {
                json["bounds", intRectangleArray].toList()
            } else {
                val c = json["c", int]
                val r = json["r", int]
                val xywh = json["xywh", rectangle]
                val x = xywh.x
                val y = xywh.y
                val dx = xywh.width / c
                val dy = xywh.height / r

                List(r * c) { Rectangle(x + it % c * dx, y + it / c * dy, dx, dy) }
            }


    return AnimationInfo(
            parseRegInfo(json, TEXTURES_PATH + json["path", string], 0, 0, 0, 0),
            json["frameDuration", float] / 1000,
            json["mode", animationPlayMode, null, Animation.PlayMode.NORMAL],
            bounds
    )
}
