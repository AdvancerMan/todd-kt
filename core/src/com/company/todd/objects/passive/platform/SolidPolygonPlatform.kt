package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.util.asset.texture.MySprite
import com.company.todd.util.box2d.bodyPattern.PolygonBodyPattern
import com.company.todd.util.box2d.bodyPattern.RectangleBodyPattern

class SolidPolygonPlatform(game: ToddGame, sprite: MySprite,
                           localVertices: Array<Vector2>, worldCenter: Vector2) :
        PassiveObject(
                game, sprite,
                RealBodyWrapper(
                        PolygonBodyPattern(
                                localVertices,
                                BodyDef.BodyType.StaticBody,
                                worldCenter
                        )
                )
        )
