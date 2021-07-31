package com.company.todd.objects.passive.platform

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.asset.texture.ToddDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.json.SerializationType
import com.company.todd.objects.base.InGameObject

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
