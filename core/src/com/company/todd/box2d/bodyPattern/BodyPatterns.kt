package com.company.todd.box2d.bodyPattern

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.box2d.bodyPattern.base.CircleBodyPattern
import com.company.todd.box2d.bodyPattern.base.PolygonBodyPattern
import com.company.todd.box2d.bodyPattern.base.RectangleBodyPattern
import com.company.todd.box2d.bodyPattern.base.combine
import com.company.todd.box2d.bodyPattern.sensor.BottomGroundSensorRectangleBodyPattern
import com.company.todd.box2d.bodyPattern.sensor.TopGroundSensorPolygonBodyPattern
import com.company.todd.box2d.bodyPattern.sensor.TopGroundSensorRectangleBodyPattern
import com.company.todd.json.serialization.toJsonValue


fun createPolygonBPWithTGS(type: BodyDef.BodyType, worldCenter: Vector2, localVertices: Array<Vector2>) =
    TopGroundSensorPolygonBodyPattern(type, worldCenter, localVertices)
        .combine(PolygonBodyPattern(type, worldCenter, localVertices))
        .withSerializer {
            it.addChild("bodyPatternType", "polygonWithTopGS".toJsonValue())
            it.addChild("b2dType", type.toJsonValue())
            it.addChild("worldBodyCenter", worldCenter.toJsonValue())
            it.addChild("localVertices", localVertices.toJsonValue { v -> v.toJsonValue() })
        }

fun createRectangleBPWithTGS(type: BodyDef.BodyType, worldPosition: Vector2,
                             size: Vector2, localCenter: Vector2 = Vector2()) =
    TopGroundSensorRectangleBodyPattern(type, worldPosition, size, localCenter)
        .combine(RectangleBodyPattern(type, worldPosition, size, localCenter))
        .withSerializer {
            it.addChild("bodyPatternType", "rectangleWithTopGS".toJsonValue())
            it.addChild("b2dType", type.toJsonValue())
            it.addChild("bodyPosition", worldPosition.toJsonValue())
            it.addChild("bodySize", size.toJsonValue())
        }

fun createRectangleBPWithTGSBGS(type: BodyDef.BodyType, worldPosition: Vector2,
                                size: Vector2, localCenter: Vector2 = Vector2()) =
    createRectangleBPWithTGS(type, worldPosition, size, localCenter)
        .combine(BottomGroundSensorRectangleBodyPattern(type, worldPosition, size, localCenter))
        .withSerializer {
            it.addChild("bodyPatternType", "rectangleWithTopGSBottomGS".toJsonValue())
            it.addChild("b2dType", type.toJsonValue())
            it.addChild("bodyPosition", worldPosition.toJsonValue())
            it.addChild("bodySize", size.toJsonValue())
        }

fun createCircleBP(type: BodyDef.BodyType, worldCenter: Vector2, radius: Float) =
    CircleBodyPattern(type, worldCenter, radius)
        .withSerializer {
            it.addChild("bodyPatternType", "circle".toJsonValue())
            it.addChild("b2dType", type.toJsonValue())
            it.addChild("bodyCenter", worldCenter.toJsonValue())
            it.addChild("bodyRadius", radius.toJsonValue())
        }
