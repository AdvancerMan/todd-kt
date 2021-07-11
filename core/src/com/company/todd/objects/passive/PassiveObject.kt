package com.company.todd.objects.passive

import com.badlogic.gdx.math.Vector2
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.BodyWrapper
import com.company.todd.objects.base.InGameObject
import com.company.todd.asset.texture.MyDrawable

abstract class PassiveObject(game: ToddGame, drawable: MyDrawable, drawableSize: Vector2?,
                             bodyLowerLeftCornerOffset: Vector2, body: BodyWrapper) :
        InGameObject(game, drawable, drawableSize, bodyLowerLeftCornerOffset, body)
