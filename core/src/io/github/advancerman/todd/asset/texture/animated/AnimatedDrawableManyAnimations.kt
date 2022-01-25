package io.github.advancerman.todd.asset.texture.animated

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import io.github.advancerman.todd.asset.texture.AnimationPackInfo
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.asset.texture.TextureManager

private val reportedBadTypes = mutableSetOf<String>()
private val reportedBadOrders = mutableSetOf<String>()
private const val LOG_TAG = "AnimatedDrawableManyAnimations"

private fun AnimationPackInfo.getDescriptor() =
    animations
        .map { (_, animationInfo) ->
            animationInfo.frameInfo.path +
                    animationInfo.bounds.map { r ->
                        listOf(r.x, r.y, r.width, r.height).map(Float::toInt)
                    }
        }
        .toString()

private fun AnimatedDrawableManyAnimations.reportBadType(
    type: AnimationType,
    packInfo: AnimationPackInfo,
) {
    val packDescriptor = packInfo.getDescriptor()
    if (!reportedBadTypes.add(packDescriptor)) {
        return
    }

    Gdx.app.error(
        LOG_TAG,
        "Trying to get non-existing animation $type for pack $drawableName: $packDescriptor"
    )
}

private fun AnimatedDrawableManyAnimations.reportBadOrder(
    type: AnimationType,
    packInfo: AnimationPackInfo,
) {
    val packDescriptor = packInfo.getDescriptor()
    if (!reportedBadOrders.add(packDescriptor)) {
        return
    }

    Gdx.app.error(
        LOG_TAG,
        "Can not find order for animation $type for pack $drawableName: $packDescriptor"
    )
}

class AnimatedDrawableManyAnimations
private constructor(private val animationPackInfo: AnimationPackInfo,
                    private val animations: Map<AnimationType, Animation<ToddDrawable>>,
                    firstAnimation: Pair<AnimationType, Animation<ToddDrawable>>) :
        AnimatedDrawable(firstAnimation.second) {
    private val reportedEvents = mutableSetOf<String>()
    private var type = firstAnimation.first

    constructor(
        animationPackInfo: AnimationPackInfo,
        animations: Map<AnimationType, Animation<ToddDrawable>>
    ) : this(
        animationPackInfo,
        animations,
        animationPackInfo.initialAnimation to
                animations[animationPackInfo.initialAnimation]!!
    )

    private fun setPlayingType(type: AnimationType, forceReset: Boolean) {
        if (forceReset || type != this.type) {
            animations[type]?.let {
                playingNow = it
                this.type = type
            } ?: reportBadType(type, animationPackInfo)
        }
    }

    override fun update(delta: Float) {
        reportedEvents.addAll(getAdditionallyReportedEvents())

        animationPackInfo.animationsOrder[type]
            .also {
                if (it == null) {
                    reportBadOrder(type, animationPackInfo)
                }
            }
            ?.firstOrNull { order -> reportedEvents.containsAll(order.on) }
            ?.let { (nextName, _, resetTime) -> setPlayingType(nextName, resetTime) }
        reportedEvents.clear()

        super.update(delta)
    }

    override fun reportEvent(eventName: String) {
        super.reportEvent(eventName)
        reportedEvents.add(eventName)
    }

    override fun getPlayingType() = type

    override fun getAdditionallyReportedEvents(): List<String> {
        return listOf(
            isAnimationFinished() to ANIMATION_FINISHED_EVENT,
            true to ALWAYS_EVENT
        )
            .filter { it.first }
            .map { it.second }
    }

    override fun dispose(manager: TextureManager) {
        manager.unload(animationPackInfo)
    }

    companion object {
        private const val ALWAYS_EVENT = "always"
        private const val ANIMATION_FINISHED_EVENT = "animationFinished"
    }
}
