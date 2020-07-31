package com.company.todd.util.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.util.box2d.bodyPattern.base.SensorName
import com.company.todd.util.box2d.bodyPattern.base.createSmoothRectangle

class RectangleSensorBodyPattern(type: BodyDef.BodyType, size: Vector2, position: Vector2,
                                 localCenter: Vector2 = Vector2(), sensorName: SensorName) :
        PolygonSensorBodyPattern(
                createSmoothRectangle(localCenter, size), type,
                position.cpy().add(size.x / 2, size.y / 2), sensorName
        )