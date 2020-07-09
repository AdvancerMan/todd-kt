package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.MySprite
import com.company.todd.util.box2d.RectangleBodyPattern

class SolidRectanglePlatform(game: ToddGame, screen: GameScreen,
                             sprite: MySprite, aabb: Rectangle) :
        PassiveObject(
                game, screen, sprite,
                RealBodyWrapper(
                        RectangleBodyPattern(
                                BodyDef.BodyType.StaticBody,
                                aabb.getSize(Vector2()),
                                aabb.getPosition(Vector2())
                        )
                )
        )
