package com.company.todd.objects.passive.platform

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.asset.texture.ToddDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.json.SerializationType
import com.company.todd.objects.base.InGameObject

@SerializationType(InGameObject::class, "solid")
open class SolidPlatform(game: ToddGame, drawable: ToddDrawable, bodyPattern: BodyPattern, scale: Float) :
    PassiveObject(game, drawable, RealBodyWrapper(bodyPattern), scale)
