package com.company.todd.util.texture

data class RegionInfo(val file: String, val x: Int, val y: Int, val w: Int, val h: Int)

class AnimationInfo(vararg val regions: Pair<RegionInfo, Int>)

class AnimationPackInfo(val animations: Map<AnimationType, AnimationInfo>) {
    constructor(vararg animations: Pair<AnimationType, AnimationInfo>) : this(mapOf(*animations))
}

enum class RegionInfos(val info: RegionInfo) {
    EXAMPLE("regionExample.png", 0, 0, 50, 20);

    constructor(file: String, x: Int, y: Int, w: Int, h: Int) : this(RegionInfo(file, x, y, w, h))
}

enum class AnimationInfos(val info: AnimationInfo) {
    EXAMPLE(
            RegionInfo("animationExample.png", 0, 0, 10, 10) to 200,
            RegionInfo("animationExample.png", 10, 0, 10, 10) to 300,
            RegionInfo("animationExample.png", 0, 10, 10, 10) to 400,
            RegionInfo("animationExample.png", 10, 10, 10, 10) to 500
    );

    constructor(vararg regions: Pair<RegionInfo, Int>) : this(AnimationInfo(*regions))
}

enum class AnimationPackInfos(val info: AnimationPackInfo) {
    EXAMPLE(
            AnimationType.STAY to AnimationInfo(
                    RegionInfo("stayExample.png", 0, 0, 10, 10) to 100
            ),
            AnimationType.RUN to AnimationInfo(
                    RegionInfo("runExample.png", 0, 0, 10, 10) to 100,
                    RegionInfo("runExample.png", 10, 0, 10, 10) to 100
            ),
            AnimationType.JUMP to AnimationInfo(
                    RegionInfo("jumpExample.png", 0, 0, 10, 10) to 100
            ),
            AnimationType.FALL to AnimationInfo(
                    RegionInfo("fallExample.png", 0, 0, 10, 10) to 100
            ),
            AnimationType.LANDING to AnimationInfo(
                    RegionInfo("landingExample.png", 0, 0, 10, 10) to 100
            ),
            AnimationType.SHOOT to AnimationInfo(
                    RegionInfo("shootExample.png", 0, 0, 10, 10) to 100
            )
    );

    constructor(vararg animations: Pair<AnimationType, AnimationInfo>) : this(AnimationPackInfo(*animations))
}
