package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.box2d.bodyPattern.sensor.createPolygonBPWithTGS

class SolidPolygonPlatform(game: ToddGame, drawable: MyDrawable,
                           localVertices: Array<Vector2>, worldCenter: Vector2,
                           drawableSize: Vector2, bodyLowerLeftCornerOffset: Vector2) :
        PassiveObject(
                game, drawable,
                RealBodyWrapper(
                        createPolygonBPWithTGS(
                                localVertices,
                                BodyDef.BodyType.StaticBody,
                                worldCenter
                        )
                ),
                drawableSize, bodyLowerLeftCornerOffset
        )
