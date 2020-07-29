package com.company.todd.objects.passive

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.BodyWrapper
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.asset.texture.MyDrawable

abstract class PassiveObject(game: ToddGame, drawable: MyDrawable, body: BodyWrapper) :
        InGameObject(game, drawable, body)
