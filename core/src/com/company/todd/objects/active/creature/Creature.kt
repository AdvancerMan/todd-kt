package com.company.todd.objects.active.creature

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.ActiveObject
import com.company.todd.util.asset.texture.MySprite
import com.company.todd.util.box2d.bodyPattern.GroundSensorBodyPattern

abstract class Creature(game: ToddGame, sprite: MySprite,
                        bodyPattern: GroundSensorBodyPattern, speed: Float, jumpPower: Float):
        ActiveObject(game, sprite, bodyPattern, speed, jumpPower) {
//     TODO shoot
//     TODO gun
}
