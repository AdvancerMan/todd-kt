package com.company.todd.objects.active.creature

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.ActiveObject
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.box2d.bodyPattern.base.BodyPattern

abstract class Creature(game: ToddGame, drawable: MyDrawable,
                        bodyPattern: BodyPattern, speed: Float,
                        jumpPower: Float, maxHealth: Float):
        ActiveObject(game, drawable, bodyPattern, speed, jumpPower, maxHealth) {
//     TODO shoot
//     TODO gun
}
