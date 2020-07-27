package com.company.todd.util.asset.texture.sprite

import com.company.todd.objects.active.ActiveObject

interface AnimationOrder {
    val type: AnimationType
    fun next(obj: ActiveObject, preferredType: AnimationType): AnimationOrder
}

private class AnimationOrderImpl(override val type: AnimationType) : AnimationOrder {
    private val instructions = mutableListOf<(ActiveObject, AnimationType) -> AnimationOrder?>()

    override fun next(obj: ActiveObject, preferredType: AnimationType) =
            instructions.map { it(obj, preferredType) }.firstOrNull { it != null } ?: this

    fun then(next: AnimationOrder, predicate: (ActiveObject, AnimationType) -> Boolean) {
        instructions.add { obj, type -> if (predicate(obj, type)) next else null }
    }
}

private object AnimationOrders {
    val stay = AnimationOrderImpl(AnimationType.STAY)
    val run = AnimationOrderImpl(AnimationType.RUN)
    val jump = AnimationOrderImpl(AnimationType.JUMP)
    val preFall = AnimationOrderImpl(AnimationType.PRE_FALL)
    val fall = AnimationOrderImpl(AnimationType.FALL)
    val fallAfterGround = AnimationOrderImpl(AnimationType.FALL_AFTER_GROUND)

    init {
        listOf(stay, run).forEach {
            it.then(jump) { _, t -> t == AnimationType.JUMP }
            it.then(fallAfterGround) { o, _ -> !o.isOnGround }
            it.then(run) { _, t -> t == AnimationType.RUN }
        }

        run.then(stay) { _, _ -> true }

        listOf(jump, preFall, fall, fallAfterGround).forEach {
            it.then(jump) { _, t -> t == AnimationType.JUMP }
            it.then(run) { o, t -> o.isOnGround && t == AnimationType.RUN }
            it.then(stay) { o, _ -> o.isOnGround }
        }

        jump.then(preFall) { o, _ -> o.getVelocity().y <= 0 }
        preFall.then(fall) { o, _ -> o.isAnimationFinished() }
    }
}

fun stayAnimation(): AnimationOrder = AnimationOrders.stay
