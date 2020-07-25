package com.company.todd.objects.active.creature

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.ActiveObject
import com.company.todd.util.asset.texture.drawable.MyDrawable
import com.company.todd.util.box2d.bodyPattern.GroundSensorBodyPattern

abstract class Creature(game: ToddGame, drawable: MyDrawable,
                        bodyPattern: GroundSensorBodyPattern, speed: Float, jumpPower: Float):
        ActiveObject(game, drawable, bodyPattern, speed, jumpPower) {
//     TODO shoot
//     TODO gun
}
