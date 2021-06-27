package com.company.todd.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.objects.base.InGameObject
import com.company.todd.box2d.bodyPattern.base.PolygonBodyPattern
import com.company.todd.box2d.bodyPattern.base.RectangleBodyPattern
import com.company.todd.box2d.bodyPattern.base.combine
import com.company.todd.json.serialization.toJsonValue

fun createPolygonBPWithTGS(type: BodyDef.BodyType, worldCenter: Vector2, localVertices: Array<Vector2>) =
        TopGroundSensorPolygonBodyPattern(type, worldCenter, localVertices)
                .combine(PolygonBodyPattern(type, worldCenter, localVertices))
                .withSerializer {
                    it.addChild("type", "polygonWithTopGS".toJsonValue())
                    it.addChild("worldBodyCenter", worldCenter.toJsonValue())
                    it.addChild("localVertices", localVertices.toJsonValue { v -> v.toJsonValue() })
                }

fun createRectangleBPWithTGS(type: BodyDef.BodyType, worldPosition: Vector2,
                             size: Vector2, localCenter: Vector2 = Vector2()) =
        TopGroundSensorRectangleBodyPattern(type, worldPosition, size, localCenter)
                .combine(RectangleBodyPattern(type, worldPosition, size, localCenter))
                .withSerializer {
                    it.addChild("type", "rectangleWithTopGS".toJsonValue())
                    it.addChild("bodyPosition", worldPosition.toJsonValue())
                    it.addChild("bodySize", size.toJsonValue())
                }

fun createRectangleBPWithTGSBGS(type: BodyDef.BodyType, worldPosition: Vector2,
                                size: Vector2, localCenter: Vector2 = Vector2()) =
        createRectangleBPWithTGS(type, worldPosition, size, localCenter)
                .combine(BottomGroundSensorRectangleBodyPattern(type, worldPosition, size, localCenter))
                .withSerializer {
                    it.addChild("type", "rectangleWithTopGSBottomGS".toJsonValue())
                    it.addChild("bodyPosition", worldPosition.toJsonValue())
                    it.addChild("bodySize", size.toJsonValue())
                }

interface TopGroundListener {
    fun beginOnGround(obj: InGameObject) {}
    fun endOnGround(obj: InGameObject) {}
}

class TopGroundSensor(private val listener: TopGroundListener) : Sensor, TopGroundListener {
    override fun beginOnGround(obj: InGameObject) = listener.beginOnGround(obj)
    override fun endOnGround(obj: InGameObject) = listener.endOnGround(obj)
}
