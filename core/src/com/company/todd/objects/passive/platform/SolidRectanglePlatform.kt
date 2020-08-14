package com.company.todd.objects.passive.platform

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.box2d.bodyPattern.base.SensorName
import com.company.todd.box2d.bodyPattern.sensor.TopGroundSensor
import com.company.todd.box2d.bodyPattern.sensor.createRectangleBPWithTGS

open class SolidRectanglePlatform(game: ToddGame, drawable: MyDrawable, drawableSize: Vector2,
                                  bodyLowerLeftCornerOffset: Vector2, pattern: BodyPattern) :
        PassiveObject(game, drawable, drawableSize, bodyLowerLeftCornerOffset, RealBodyWrapper(pattern)) {

    constructor(game: ToddGame, drawable: MyDrawable, drawableSize: Vector2,
                bodyLowerLeftCornerOffset: Vector2, bodyPosition: Vector2, bodySize: Vector2) :
            this(
                    game, drawable, drawableSize, bodyLowerLeftCornerOffset,
                    createRectangleBPWithTGS(
                            BodyDef.BodyType.StaticBody,
                            bodyPosition, bodySize
                    )
            )

    init {
        // it is guaranteed that link to this is not used by sensor while this creates
        @Suppress("LeakingThis")
        pattern.sensors[SensorName.TOP_GROUND_SENSOR] = TopGroundSensor(this)
    }
}
