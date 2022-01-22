package io.github.advancerman.todd.asset.texture

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.asset.texture.animated.AnimationType
import io.github.advancerman.todd.json.ManualJsonConstructor
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.json.deserialization.*

sealed class DrawableInfo

@SerializationType([DrawableInfo::class, RegionInfo::class])
open class RegionInfo(
    val path: String,
    val x: Int,
    val y: Int,
    val w: Int,
    val h: Int
) :
    DrawableInfo() {
    open fun copy(x: Int, y: Int, w: Int, h: Int) =
        RegionInfo(path, x, y, w, h)

    companion object {
        @ManualJsonConstructor
        private fun manualConstructor(json: JsonValue, parsed: MutableMap<String, Any?>) {
            TextureLoader.constructRegionInfo(json, parsed)
        }
    }
}

@SerializationType([DrawableInfo::class, RegionInfo::class], "TiledRegionInfo")
class TiledRegionInfo(
    path: String,
    x: Int,
    y: Int,
    w: Int,
    h: Int
) : RegionInfo(path, x, y, w, h) {
    override fun copy(x: Int, y: Int, w: Int, h: Int) =
        TiledRegionInfo(path, x, y, w, h)
}

@SerializationType([DrawableInfo::class, RegionInfo::class], "NineTiledRegionInfo")
class NineTiledRegionInfo(
    path: String,
    x: Int,
    y: Int,
    w: Int,
    h: Int,
    val lw: Int,
    val rw: Int,
    val uh: Int,
    val dh: Int
) : RegionInfo(path, x, y, w, h) {
    override fun copy(x: Int, y: Int, w: Int, h: Int) =
        NineTiledRegionInfo(path, x, y, w, h, lw, rw, uh, dh)

    companion object {
        @ManualJsonConstructor
        private fun manualConstructor(json: JsonValue, parsed: MutableMap<String, Any?>) {
            TextureLoader.constructNineTiledRegionInfo(json, parsed)
        }
    }
}

@SerializationType([DrawableInfo::class, RegionInfo::class], "CoveredTiledRegionInfo")
class CoveredTiledRegionInfo(
    path: String,
    x: Int,
    y: Int,
    w: Int,
    h: Int,
    val uh: Int
) : RegionInfo(path, x, y, w, h) {
    override fun copy(x: Int, y: Int, w: Int, h: Int) =
        CoveredTiledRegionInfo(path, x, y, w, h, uh)
}

@SerializationType([AnimationInfo::class])
data class AnimationInfo(
    val frameInfo: RegionInfo,
    val frameDuration: Float,
    val mode: Animation.PlayMode,
    val bounds: List<Rectangle>
) : DrawableInfo() {
    companion object {
        @ManualJsonConstructor
        fun manualConstructor(json: JsonValue, parsed: MutableMap<String, Any?>) {
            TextureLoader.constructAnimationInfo(json, parsed)
        }
    }
}

// A trick to have different serialization types with different base classes for one class
object AnimationInfoParser {
    @SerializationType([DrawableInfo::class], "AnimationInfo")
    fun createAnimationInfo(
        frameInfo: RegionInfo,
        frameDuration: Float,
        mode: Animation.PlayMode,
        bounds: List<Rectangle>
    ) = AnimationInfo(frameInfo, frameDuration, mode, bounds)

    @ManualJsonConstructor("createAnimationInfo")
    private fun manualConstructor(json: JsonValue, parsed: MutableMap<String, Any?>) {
        AnimationInfo.manualConstructor(json, parsed)
    }
}

@SerializationType([DrawableInfo::class], "AnimationPackInfo")
data class AnimationPackInfo(val animations: Map<AnimationType, AnimationInfo>) : DrawableInfo() {
    companion object {
        @ManualJsonConstructor
        private fun manualConstructor(json: JsonValue, parsed: MutableMap<String, Any?>) {
            TextureLoader.constructAnimationPackInfo(json, parsed)
        }
    }
}
