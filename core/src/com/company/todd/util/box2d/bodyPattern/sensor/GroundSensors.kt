package com.company.todd.util.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.objects.base.InGameObject
import com.company.todd.util.box2d.bodyPattern.base.PolygonBodyPattern
import com.company.todd.util.box2d.bodyPattern.base.RectangleBodyPattern
import com.company.todd.util.box2d.bodyPattern.base.combine

fun createPolygonBPWithTGS(type: BodyDef.BodyType, worldCenter: Vector2, localVertices: Array<Vector2>) =
        TopGroundSensorPolygonBodyPattern(type, worldCenter, localVertices)
                .combine(PolygonBodyPattern(type, worldCenter, localVertices))

fun createRectangleBPWithTGS(type: BodyDef.BodyType, worldPosition: Vector2,
                             size: Vector2, localCenter: Vector2 = Vector2()) =
        TopGroundSensorRectangleBodyPattern(type, worldPosition, size, localCenter)
                .combine(RectangleBodyPattern(type, worldPosition, size, localCenter))

fun createRectangleBPWithTGSBGS(type: BodyDef.BodyType, worldPosition: Vector2,
                                size: Vector2, localCenter: Vector2 = Vector2()) =
        createRectangleBPWithTGS(type, worldPosition, size, localCenter)
                .combine(BottomGroundSensorRectangleBodyPattern(type, worldPosition, size, localCenter))

interface TopGroundListener {
    fun beginOnGround(obj: InGameObject) {}
    fun endOnGround(obj: InGameObject) {}
}

class TopGroundSensor(private val listener: TopGroundListener) : Sensor, TopGroundListener {
    override fun beginOnGround(obj: InGameObject) = listener.beginOnGround(obj)
    override fun endOnGround(obj: InGameObject) = listener.endOnGround(obj)
}
