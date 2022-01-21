package io.github.advancerman.todd.json.deserialization

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.asset.texture.*
import io.github.advancerman.todd.json.JsonDefaults
import io.github.advancerman.todd.json.deserialization.exception.DeserializationException
import io.github.advancerman.todd.util.TEXTURES_PATH
import io.github.advancerman.todd.util.files.crawlJsonListsWithComments

fun loadTextureInfos(): Map<String, DrawableInfo> {
    val res = mutableMapOf<String, DrawableInfo>()

    crawlJsonListsWithComments(TEXTURES_PATH).forEach { json ->
        checkName(json, res.keys)
        res[json["name", string]] = json.construct()
    }

    return res
}

object TextureLoader {
    private const val LOG_TAG = "TextureLoader"

    private fun parseIntRectangle(
        jsonName: String,
        x: String,
        y: String,
        w: String,
        h: String,
        json: JsonValue,
        parsed: MutableMap<String, Pair<Any?, Boolean>>
    ) {
        if (json[jsonName] == null) {
            return
        }

        val xywh = json[jsonName, intRectangle]
        JsonDefaults.setDefault(x, xywh.x.toInt(), parsed)
        JsonDefaults.setDefault(y, xywh.y.toInt(), parsed)
        JsonDefaults.setDefault(w, xywh.width.toInt(), parsed)
        JsonDefaults.setDefault(h, xywh.height.toInt(), parsed)
    }

    private fun parseXywh(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        parseIntRectangle("xywh", "x", "y", "w", "h", json, parsed)
    }

    fun constructRegionInfo(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        parseXywh(json, parsed)
    }

    fun constructNineTiledRegionInfo(
        json: JsonValue,
        parsed: MutableMap<String, Pair<Any?, Boolean>>
    ) {
        parseIntRectangle("lrud", "lw", "rw", "uh", "dh", json, parsed)
    }

    fun constructAnimationInfo(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        checkContains(json, "frameInfo", "frame info without xywh")
        val frameInfo = json["frameInfo"]
        frameInfo.removeAll { it.name == "xywh" }
        frameInfo.addChild("x", JsonValue(0))
        frameInfo.addChild("y", JsonValue(0))
        frameInfo.addChild("w", JsonValue(0))
        frameInfo.addChild("h", JsonValue(0))
        parsed["frameInfo"] = frameInfo.construct<RegionInfo>() to true

        try {
            val bounds = if (json.has("bounds")) {
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
            parsed["bounds"] = bounds to true
        } catch (e: DeserializationException) {
            Gdx.app.error(LOG_TAG, "Could not parse bounds", e)
        }

        // TODO add as a component to json primitives
        parsed["mode"] = (
                json["mode"]?.asString()
                    ?.let(Animation.PlayMode::valueOf)
                    ?: Animation.PlayMode.NORMAL
                ) to true
    }

    fun constructAnimationPackInfo(
        json: JsonValue,
        parsed: MutableMap<String, Pair<Any?, Boolean>>
    ) {
        parsed["animations"] = json["animations"]
            ?.associate { it.name to it.construct<AnimationInfo>() }
            .let { it to (it != null) }
    }
}
