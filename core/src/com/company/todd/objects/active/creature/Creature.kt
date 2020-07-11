package com.company.todd.objects.active.creature

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.ActiveObject
import com.company.todd.objects.base.BodyWrapper
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.MySprite

abstract class Creature(game: ToddGame, sprite: MySprite, body: BodyWrapper, speed: Float, jumpPower: Float):
        ActiveObject(game, sprite, body, speed, jumpPower) {
//     TODO shoot
//     TODO gun
}
