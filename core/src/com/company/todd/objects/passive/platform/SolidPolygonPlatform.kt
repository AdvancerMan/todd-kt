package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.sensor.createPolygonBPWithTGS
import com.company.todd.json.SerializationType

@SerializationType("solidPolygon")
class SolidPolygonPlatform(game: ToddGame, drawable: MyDrawable,
                           drawableSize: Vector2, bodyLowerLeftCornerOffset: Vector2,
                           worldBodyCenter: Vector2, localVertices: Array<Vector2>) :
        PassiveObject(
                game, drawable, drawableSize, bodyLowerLeftCornerOffset,
                RealBodyWrapper(
                        createPolygonBPWithTGS(
                                BodyDef.BodyType.StaticBody,
                                worldBodyCenter,
                                localVertices
                        )
                )
        )
