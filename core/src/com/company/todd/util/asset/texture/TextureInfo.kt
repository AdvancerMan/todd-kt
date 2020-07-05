package com.company.todd.util.asset.texture

import com.badlogic.gdx.math.Rectangle

data class RegionInfo(val fileName: String, val x: Float, val y: Float, val w: Float, val h: Float)
data class AnimationInfo(val fileName: String, val frameDuration: Float, val bounds: List<Rectangle>)
data class AnimationPackInfo(val animations: List<Pair<AnimationType, AnimationInfo>>)
