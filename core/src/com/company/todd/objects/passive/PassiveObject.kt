package com.company.todd.objects.passive

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.BodyWrapper
import com.company.todd.objects.base.InGameObject
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.MySprite

abstract class PassiveObject(game: ToddGame, sprite: MySprite, body: BodyWrapper) :
        InGameObject(game, sprite, body)
