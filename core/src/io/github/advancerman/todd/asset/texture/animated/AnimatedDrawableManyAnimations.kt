package io.github.advancerman.todd.asset.texture.animated

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import io.github.advancerman.todd.asset.texture.AnimationPackInfo
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.asset.texture.TextureManager

private val reportedBadTypes = mutableSetOf<String>()

private fun AnimatedDrawableManyAnimations.reportBadType(type: AnimationType, packInfo: AnimationPackInfo) {
    val packDescriptor = packInfo.animations.map {
        it.second.frameInfo.path + it.second.bounds
            .map { r -> listOf(r.x, r.y, r.width, r.height).map(Float::toInt) }
    }.toString()
    if (!reportedBadTypes.add(packDescriptor)) {
        return
    }

    Gdx.app.error(
        "AnimatedSprite",
        "Trying to get non-existing animation $type for pack $drawableName: $packDescriptor"
    )
}

class AnimatedDrawableManyAnimations
private constructor(private val animationPackInfo: AnimationPackInfo,
                    private val animations: Map<AnimationType, Animation<ToddDrawable>>,
                    firstAnimation: Pair<AnimationType, Animation<ToddDrawable>>) :
        AnimatedDrawable(firstAnimation.second) {
    private var type = firstAnimation.first

    constructor(animationPackInfo: AnimationPackInfo,
                animations: Map<AnimationType, Animation<ToddDrawable>>) :
            this(
                    animationPackInfo, animations,
                    animations["STAY"]?.let { "STAY" to it }
                            ?: animations.entries.firstOrNull()?.let { it.key to it.value }
                            ?: throw IllegalArgumentException("Animation packs without animations aren't allowed")
            )

    override fun setPlayingType(type: AnimationType, forceReset: Boolean) {
        if (forceReset || type != this.type) {
            animations[type]?.let {
                playingNow = it
                this.type = type
            } ?: reportBadType(type, animationPackInfo)
        }
    }

    override fun getPlayingType() = type

    override fun hasAnimationType(type: AnimationType) =
            animations.containsKey(type)

    override fun dispose(manager: TextureManager) {
        manager.unload(animationPackInfo)
    }
}
