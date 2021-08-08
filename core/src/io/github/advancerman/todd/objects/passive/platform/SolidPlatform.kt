package io.github.advancerman.todd.objects.passive.platform

import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.RealBodyWrapper
import io.github.advancerman.todd.objects.passive.PassiveObject
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.box2d.bodyPattern.base.BodyPattern
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.objects.base.InGameObject

/**
 * Simple platform which normally can not be passed through
 *
 * @param drawable Base drawable for InGameObject.
 *                 Z-index is relative to object's actor,
 *                 offset is relative to unrotated, unflipped actor position
 * @param bodyPattern Body description for InGameObject
 * @param scale Actor's scale
 */
@SerializationType(InGameObject::class, "Solid")
open class SolidPlatform(game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern, scale: Float = 1f) :
    PassiveObject(game, drawable, RealBodyWrapper(bodyPattern), scale)
