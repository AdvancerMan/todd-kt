package com.company.todd.box2d.bodyPattern

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.box2d.bodyPattern.base.*
import com.company.todd.box2d.bodyPattern.sensor.BottomGroundSensorRectangleBodyPattern
import com.company.todd.box2d.bodyPattern.sensor.TopGroundSensorPolygonBodyPattern
import com.company.todd.box2d.bodyPattern.sensor.TopGroundSensorRectangleBodyPattern
import com.company.todd.json.JsonDefaults
import com.company.todd.json.ManualJsonConstructor
import com.company.todd.json.SerializationType
import com.company.todd.json.deserialization.get
import com.company.todd.json.deserialization.vectorArray
import com.company.todd.json.serialization.toJsonValue

object BodyPatterns {
    @SerializationType("bodyPattern", "polygonWithTopGS")
    fun createPolygonBPWithTGS(
        b2dType: BodyDef.BodyType,
        worldCenter: Vector2,
        localVertices: Array<Vector2>
    ): BodyPattern =
        TopGroundSensorPolygonBodyPattern(b2dType, worldCenter, localVertices)
            .combine(PolygonBodyPattern(b2dType, worldCenter, localVertices))
            .withSerializer {
                it.addChild("type", "polygonWithTopGS".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldCenter", worldCenter.toJsonValue())
                it.addChild("localVertices", localVertices.toJsonValue { v -> v.toJsonValue() })
            }

    @ManualJsonConstructor("createPolygonBPWithTGS")
    private fun polygonBPWithTGSManualConstructor(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        JsonDefaults.setDefault("worldBodyCenter", Vector2(), parsed)
        if (!parsed["localVertices"]!!.second) {
            parsed["localVertices"] = json["localVertices", vectorArray] to true
        }
    }

    @SerializationType("bodyPattern", "rectangleWithTopGS")
    fun createRectangleBPWithTGS(
        b2dType: BodyDef.BodyType, worldPosition: Vector2,
        size: Vector2, localCenter: Vector2
    ): BodyPattern =
        TopGroundSensorRectangleBodyPattern(b2dType, worldPosition, size, localCenter)
            .combine(RectangleBodyPattern(b2dType, worldPosition, size, localCenter))
            .withSerializer {
                it.addChild("type", "rectangleWithTopGS".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldPosition", worldPosition.toJsonValue())
                it.addChild("size", size.toJsonValue())
                it.addChild("localCenter", localCenter.toJsonValue())
            }

    @ManualJsonConstructor("createRectangleBPWithTGS")
    private fun rectangleBPWithTGSDefaults(
        @Suppress("UNUSED_PARAMETER") json: JsonValue,
        parsed: MutableMap<String, Pair<Any?, Boolean>>
    ) {
        JsonDefaults.setDefault("worldPosition", Vector2(), parsed)
        JsonDefaults.setDefault("localCenter", Vector2(), parsed)
    }

    @SerializationType("bodyPattern", "rectangleWithTopGSBottomGS")
    fun createRectangleBPWithTGSBGS(
        b2dType: BodyDef.BodyType, worldPosition: Vector2,
        size: Vector2, localCenter: Vector2
    ): BodyPattern =
        createRectangleBPWithTGS(b2dType, worldPosition, size, localCenter)
            .combine(BottomGroundSensorRectangleBodyPattern(b2dType, worldPosition, size, localCenter))
            .withSerializer {
                it.addChild("type", "rectangleWithTopGSBottomGS".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldPosition", worldPosition.toJsonValue())
                it.addChild("size", size.toJsonValue())
                it.addChild("localCenter", localCenter.toJsonValue())
            }

    @ManualJsonConstructor("createRectangleBPWithTGSBGS")
    private fun rectangleBPWithTGSBGSDefaults(
        @Suppress("UNUSED_PARAMETER") json: JsonValue,
        parsed: MutableMap<String, Pair<Any?, Boolean>>
    ) {
        JsonDefaults.setDefault("worldPosition", Vector2(), parsed)
        JsonDefaults.setDefault("localCenter", Vector2(), parsed)
    }

    @SerializationType("bodyPattern", "circle")
    fun createCircleBP(b2dType: BodyDef.BodyType, worldCenter: Vector2, radius: Float): BodyPattern =
        CircleBodyPattern(b2dType, worldCenter, radius)
            .withSerializer {
                it.addChild("type", "circle".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldCenter", worldCenter.toJsonValue())
                it.addChild("radius", radius.toJsonValue())
            }

    @ManualJsonConstructor("createCircleBP")
    private fun circleBPDefaults(
        @Suppress("UNUSED_PARAMETER") json: JsonValue,
        parsed: MutableMap<String, Pair<Any?, Boolean>>
    ) {
        JsonDefaults.setDefault("worldCenter", Vector2(), parsed)
    }
}
