package io.github.advancerman.todd.asset.texture.animated

import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.creature.Creature
import io.github.advancerman.todd.objects.passive.interactive.Portal

interface AnimationEvent {
    val name: String
}

/**
 * Entity that passed to animation order manager when specific event happens.
 *
 * * ALWAYS - always passed to the animation
 * * ANIMATION_FINISHED - passed continuously when animation is finished
 *
 * 
 * * FALL - passed continuously when [Creature]'s y velocity is less than 0
 * * ON_GROUND - passed continuously when [Creature] is detected as 'touching ground'
 *
 * 
 * * MOVE - passed continuously when [Creature]'s behaviour performs horizontal move
 * * JUMP - passed once when [Creature]'s behaviour performs a jump
 * * ATTACK - passed once when [Creature]'s behaviour performs an attack
 * * PREPARATION - passed continuously when [Creature]'s behaviour performs a preparation for attack
 *
 * 
 * * DETECT_ENTITY - passed once, when [Portal] detects some entity to teleport
 * * TELEPORT_ENTITY - passed once, when [Portal] teleports some entity
 */
@SerializationType([AnimationEvent::class])
enum class ToddAnimationEvent : AnimationEvent {
    ALWAYS,
    ANIMATION_FINISHED,

    FALL,
    ON_GROUND,

    HORIZONTAL_MOVE,
    JUMP,
    ATTACK,
    ATTACK_PREPARATION,

    DETECT_ENTITY,
    TELEPORT_ENTITY,
}
