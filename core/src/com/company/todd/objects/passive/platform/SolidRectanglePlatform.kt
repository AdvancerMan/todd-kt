package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.box2d.bodyPattern.sensor.createRectangleBPWithTGS

open class SolidRectanglePlatform(game: ToddGame, drawable: MyDrawable, aabb: Rectangle) :
        PassiveObject(
                game, drawable,
                RealBodyWrapper(
                        createRectangleBPWithTGS(
                                BodyDef.BodyType.StaticBody,
                                aabb.getSize(Vector2()),
                                aabb.getPosition(Vector2())
                        )
                )
        )
