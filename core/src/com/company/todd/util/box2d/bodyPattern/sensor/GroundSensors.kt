package com.company.todd.util.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.util.box2d.bodyPattern.base.PolygonBodyPattern
import com.company.todd.util.box2d.bodyPattern.base.RectangleBodyPattern
import com.company.todd.util.box2d.bodyPattern.base.SensorName
import com.company.todd.util.box2d.bodyPattern.base.combine

fun createRectangleBPWithBGS(type: BodyDef.BodyType, size: Vector2,
                             position: Vector2, localCenter: Vector2 = Vector2()) =
        BottomGroundSensorRectangleBodyPattern(type, size, position, localCenter)
                .combine(RectangleBodyPattern(type, size, position, localCenter))

class TopGroundSensor : Sensor

fun createPolygonBPWithTGS(localVertices: Array<Vector2>, type: BodyDef.BodyType, center: Vector2) =
        TopGroundSensorPolygonBodyPattern(localVertices, type, center)
                .combine(PolygonBodyPattern(localVertices, type, center))
                .apply { sensors[SensorName.TOP_GROUND_SENSOR] = TopGroundSensor() }

fun createRectangleBPWithTGS(type: BodyDef.BodyType, size: Vector2,
                             position: Vector2, localCenter: Vector2 = Vector2()) =
        TopGroundSensorRectangleBodyPattern(type, size, position, localCenter)
                .combine(RectangleBodyPattern(type, size, position, localCenter))
                .apply { sensors[SensorName.TOP_GROUND_SENSOR] = TopGroundSensor() }

fun createRectangleBPWithTGSBGS(type: BodyDef.BodyType, size: Vector2,
                                position: Vector2, localCenter: Vector2 = Vector2()) =
        createRectangleBPWithTGS(type, size, position, localCenter)
                .combine(BottomGroundSensorRectangleBodyPattern(type, size, position, localCenter))
