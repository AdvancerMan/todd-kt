package com.company.todd.objects.passive

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.BodyWrapper
import com.company.todd.objects.base.InGameObject
import com.company.todd.asset.texture.ToddDrawable

abstract class PassiveObject(game: ToddGame, drawable: ToddDrawable, body: BodyWrapper, scale: Float) :
    InGameObject(game, drawable, body, scale)
