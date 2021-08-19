package io.github.advancerman.todd.asset.texture.animated

import io.github.advancerman.todd.objects.creature.Creature

interface AnimationOrder {
    val type: AnimationType
    fun next(obj: Creature, preferredType: AnimationType): AnimationOrder
}

private class AnimationOrderImpl(override val type: AnimationType) : AnimationOrder {
    private val instructions = mutableListOf<(Creature, AnimationType) -> AnimationOrder?>()

    override fun next(obj: Creature, preferredType: AnimationType) =
            instructions.map { it(obj, preferredType) }.firstOrNull { it != null } ?: this

    fun then(next: AnimationOrder, predicate: (Creature, AnimationType) -> Boolean) {
        instructions.add { obj, type -> if (predicate(obj, type)) next else null }
    }
}

private object AnimationOrders {
    val stay = AnimationOrderImpl("STAY")
    val run = AnimationOrderImpl("RUN")
    val jump = AnimationOrderImpl("JUMP")
    val preFall = AnimationOrderImpl("PRE_FALL")
    val fall = AnimationOrderImpl("FALL")
    val fallAfterGround = AnimationOrderImpl("FALL_AFTER_GROUND")

    init {
        listOf(stay, run).forEach {
            it.then(jump) { _, t -> t == "JUMP" }
            it.then(fallAfterGround) { o, _ -> !o.isOnGround }
            it.then(run) { _, t -> t == "RUN" }
        }

        run.then(stay) { _, _ -> true }

        listOf(jump, preFall, fall, fallAfterGround).forEach {
            it.then(jump) { _, t -> t == "JUMP" }
            it.then(run) { o, t -> o.isOnGround && t == "RUN" }
            it.then(stay) { o, _ -> o.isOnGround }
        }

        jump.then(preFall) { o, _ -> o.body.getVelocity().y <= 0 }
        preFall.then(fall) { o, _ -> o.drawable.isAnimationFinished() }
    }
}

fun stayAnimation(): AnimationOrder = AnimationOrders.stay
