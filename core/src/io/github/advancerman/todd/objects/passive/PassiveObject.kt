package io.github.advancerman.todd.objects.passive

import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.BodyWrapper
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.asset.texture.ToddDrawable

abstract class PassiveObject(game: ToddGame, drawable: ToddDrawable, body: BodyWrapper, scale: Float) :
    InGameObject(game, drawable, body, scale)
