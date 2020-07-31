package com.company.todd.util.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Contact
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.box2d.bodyPattern.base.PolygonBodyPattern
import com.company.todd.util.box2d.bodyPattern.base.RectangleBodyPattern
import com.company.todd.util.box2d.bodyPattern.base.SensorName
import com.company.todd.util.box2d.bodyPattern.base.combine

fun createRectangleBPWithBGS(type: BodyDef.BodyType, size: Vector2,
                             position: Vector2, localCenter: Vector2 = Vector2()) =
        BottomGroundSensorRectangleBodyPattern(type, size, position, localCenter)
                .combine(RectangleBodyPattern(type, size, position, localCenter))

fun createPolygonBPWithTGS(localVertices: Array<Vector2>, type: BodyDef.BodyType, center: Vector2) =
        TopGroundSensorPolygonBodyPattern(localVertices, type, center)
                .combine(PolygonBodyPattern(localVertices, type, center))

fun createRectangleBPWithTGS(type: BodyDef.BodyType, size: Vector2,
                             position: Vector2, localCenter: Vector2 = Vector2()) =
        TopGroundSensorRectangleBodyPattern(type, size, position, localCenter)
                .combine(RectangleBodyPattern(type, size, position, localCenter))

fun createRectangleBPWithTGSBGS(type: BodyDef.BodyType, size: Vector2,
                                position: Vector2, localCenter: Vector2 = Vector2()) =
        createRectangleBPWithTGS(type, size, position, localCenter)
                .combine(BottomGroundSensorRectangleBodyPattern(type, size, position, localCenter))

interface TopGroundListener {
    fun beginOnGround(obj: InGameObject) {}
    fun endOnGround(obj: InGameObject) {}
}

class TopGroundSensor(private val listener: TopGroundListener) : Sensor, TopGroundListener {
    override fun beginOnGround(obj: InGameObject) = listener.beginOnGround(obj)
    override fun endOnGround(obj: InGameObject) = listener.endOnGround(obj)
}
