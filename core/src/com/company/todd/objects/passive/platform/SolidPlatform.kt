package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.Vector2
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.json.SerializationType

@SerializationType("igo", "solid")
open class SolidPlatform(
    game: ToddGame, drawable: MyDrawable, drawableSize: Vector2?,
    bodyLowerLeftCornerOffset: Vector2, bodyPattern: BodyPattern
) : PassiveObject(game, drawable, drawableSize, bodyLowerLeftCornerOffset, RealBodyWrapper(bodyPattern))
