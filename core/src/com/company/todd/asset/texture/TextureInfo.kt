package com.company.todd.asset.texture

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Rectangle
import com.company.todd.asset.texture.animated.AnimationType

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
