package com.company.todd.objects.passive.platform

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.json.SerializationType

@SerializationType("igo", "solid")
open class SolidPlatform(game: ToddGame, drawable: MyDrawable, bodyPattern: BodyPattern) :
    PassiveObject(game, drawable, RealBodyWrapper(bodyPattern))
