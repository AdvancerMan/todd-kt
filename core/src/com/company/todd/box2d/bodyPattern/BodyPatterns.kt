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
        worldBodyCenter: Vector2,
        localBodyVertices: Array<Vector2>
    ): BodyPattern =
        TopGroundSensorPolygonBodyPattern(b2dType, worldBodyCenter, localBodyVertices)
            .combine(PolygonBodyPattern(b2dType, worldBodyCenter, localBodyVertices))
            .withSerializer {
                it.addChild("bodyPatternType", "polygonWithTopGS".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldBodyCenter", worldBodyCenter.toJsonValue())
                it.addChild("localBodyVertices", localBodyVertices.toJsonValue { v -> v.toJsonValue() })
            }

    @ManualJsonConstructor("createPolygonBPWithTGS")
    private fun polygonBPWithTGSManualConstructor(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        JsonDefaults.setDefault("worldBodyCenter", Vector2(), parsed)
        if (!parsed["localBodyVertices"]!!.second) {
            parsed["localBodyVertices"] = json["localBodyVertices", vectorArray] to true
        }
    }

    @SerializationType("bodyPattern", "rectangleWithTopGS")
    fun createRectangleBPWithTGS(
        b2dType: BodyDef.BodyType, worldBodyPosition: Vector2,
        bodySize: Vector2, localBodyCenter: Vector2
    ): BodyPattern =
        TopGroundSensorRectangleBodyPattern(b2dType, worldBodyPosition, bodySize, localBodyCenter)
            .combine(RectangleBodyPattern(b2dType, worldBodyPosition, bodySize, localBodyCenter))
            .withSerializer {
                it.addChild("bodyPatternType", "rectangleWithTopGS".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldBodyPosition", worldBodyPosition.toJsonValue())
                it.addChild("bodySize", bodySize.toJsonValue())
                it.addChild("localBodyCenter", localBodyCenter.toJsonValue())
            }

    @ManualJsonConstructor("createRectangleBPWithTGS")
    private fun rectangleBPWithTGSDefaults(
        @Suppress("UNUSED_PARAMETER") json: JsonValue,
        parsed: MutableMap<String, Pair<Any?, Boolean>>
    ) {
        JsonDefaults.setDefault("worldBodyPosition", Vector2(), parsed)
        JsonDefaults.setDefault("localBodyCenter", Vector2(), parsed)
    }

    @SerializationType("bodyPattern", "rectangleWithTopGSBottomGS")
    fun createRectangleBPWithTGSBGS(
        b2dType: BodyDef.BodyType, worldBodyPosition: Vector2,
        bodySize: Vector2, localBodyCenter: Vector2
    ): BodyPattern =
        createRectangleBPWithTGS(b2dType, worldBodyPosition, bodySize, localBodyCenter)
            .combine(BottomGroundSensorRectangleBodyPattern(b2dType, worldBodyPosition, bodySize, localBodyCenter))
            .withSerializer {
                it.addChild("bodyPatternType", "rectangleWithTopGSBottomGS".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldBodyPosition", worldBodyPosition.toJsonValue())
                it.addChild("bodySize", bodySize.toJsonValue())
                it.addChild("localBodyCenter", localBodyCenter.toJsonValue())
            }

    @ManualJsonConstructor("createRectangleBPWithTGSBGS")
    private fun rectangleBPWithTGSBGSDefaults(
        @Suppress("UNUSED_PARAMETER") json: JsonValue,
        parsed: MutableMap<String, Pair<Any?, Boolean>>
    ) {
        JsonDefaults.setDefault("worldBodyPosition", Vector2(), parsed)
        JsonDefaults.setDefault("localBodyCenter", Vector2(), parsed)
    }

    @SerializationType("bodyPattern", "circle")
    fun createCircleBP(b2dType: BodyDef.BodyType, worldBodyCenter: Vector2, bodyRadius: Float): BodyPattern =
        CircleBodyPattern(b2dType, worldBodyCenter, bodyRadius)
            .withSerializer {
                it.addChild("bodyPatternType", "circle".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldBodyCenter", worldBodyCenter.toJsonValue())
                it.addChild("bodyRadius", bodyRadius.toJsonValue())
            }

    @ManualJsonConstructor("createCircleBP")
    private fun circleBPDefaults(
        @Suppress("UNUSED_PARAMETER") json: JsonValue,
        parsed: MutableMap<String, Pair<Any?, Boolean>>
    ) {
        JsonDefaults.setDefault("worldBodyCenter", Vector2(), parsed)
    }
}
