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
    @SerializationType(BodyPattern::class, "polygonWithTopGS")
    fun createPolygonBPWithTGS(
        b2dType: BodyDef.BodyType, worldCenter: Vector2, localVertices: Array<Vector2>, scale: Float
    ): BodyPattern {
        val scaledLocalVertices = localVertices.map { it.cpy().scl(scale) }.toTypedArray()
        return TopGroundSensorPolygonBodyPattern(b2dType, worldCenter, scaledLocalVertices)
            .combine(PolygonBodyPattern(b2dType, worldCenter, scaledLocalVertices))
            .withSerializer {
                it.addChild("type", "polygonWithTopGS".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldCenter", worldCenter.toJsonValue())
                it.addChild("localVertices", localVertices.toJsonValue { v -> v.toJsonValue() })
                it.addChild("scale", scale.toJsonValue())
            }
    }

    @ManualJsonConstructor("createPolygonBPWithTGS")
    private fun polygonBPWithTGSManualConstructor(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        JsonDefaults.setDefault("worldBodyCenter", Vector2(), parsed)
        if (!parsed["localVertices"]!!.second) {
            parsed["localVertices"] = json["localVertices", vectorArray] to true
        }
        parseScale(json, parsed)
    }

    @SerializationType(BodyPattern::class, "rectangleWithTopGS")
    fun createRectangleBPWithTGS(
        b2dType: BodyDef.BodyType, worldPosition: Vector2,
        size: Vector2, localCenter: Vector2, scale: Float
    ): BodyPattern {
        val scaledSize = size.cpy().scl(scale)
        val scaledCenter = localCenter.cpy().scl(scale)
        return TopGroundSensorRectangleBodyPattern(b2dType, worldPosition, scaledSize, scaledCenter)
            .combine(RectangleBodyPattern(b2dType, worldPosition, scaledSize, scaledCenter))
            .withSerializer {
                it.addChild("type", "rectangleWithTopGS".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldPosition", worldPosition.toJsonValue())
                it.addChild("size", size.toJsonValue())
                it.addChild("localCenter", localCenter.toJsonValue())
                it.addChild("scale", scale.toJsonValue())
            }
    }

    @ManualJsonConstructor("createRectangleBPWithTGS")
    private fun rectangleBPWithTGSDefaults(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        JsonDefaults.setDefault("worldPosition", Vector2(), parsed)
        JsonDefaults.setDefault("localCenter", Vector2(), parsed)
        parseScale(json, parsed)
    }

    @SerializationType(BodyPattern::class, "rectangleWithTopGSBottomGS")
    fun createRectangleBPWithTGSBGS(
        b2dType: BodyDef.BodyType, worldPosition: Vector2,
        size: Vector2, localCenter: Vector2, scale: Float
    ): BodyPattern {
        val scaledSize = size.cpy().scl(scale)
        val scaledCenter = localCenter.cpy().scl(scale)
        return RectangleBodyPattern(b2dType, worldPosition, scaledSize, scaledCenter)
            .combine(TopGroundSensorRectangleBodyPattern(b2dType, worldPosition, scaledSize, scaledCenter))
            .combine(BottomGroundSensorRectangleBodyPattern(b2dType, worldPosition, scaledSize, scaledCenter))
            .withSerializer {
                it.addChild("type", "rectangleWithTopGSBottomGS".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldPosition", worldPosition.toJsonValue())
                it.addChild("size", size.toJsonValue())
                it.addChild("localCenter", localCenter.toJsonValue())
                it.addChild("scale", scale.toJsonValue())
            }
    }

    @ManualJsonConstructor("createRectangleBPWithTGSBGS")
    private fun rectangleBPWithTGSBGSDefaults(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        JsonDefaults.setDefault("worldPosition", Vector2(), parsed)
        JsonDefaults.setDefault("localCenter", Vector2(), parsed)
        parseScale(json, parsed)
    }

    @SerializationType(BodyPattern::class, "circle")
    fun createCircleBP(b2dType: BodyDef.BodyType, worldCenter: Vector2, radius: Float, scale: Float): BodyPattern =
        CircleBodyPattern(b2dType, worldCenter, radius * scale)
            .withSerializer {
                it.addChild("type", "circle".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldCenter", worldCenter.toJsonValue())
                it.addChild("radius", radius.toJsonValue())
                it.addChild("scale", scale.toJsonValue())
            }

    @ManualJsonConstructor("createCircleBP")
    private fun circleBPDefaults(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        JsonDefaults.setDefault("worldCenter", Vector2(), parsed)
        parseScale(json, parsed)
    }

    private fun parseScale(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        if (parsed["scale"]?.second != true) {
            parsed["scale"] = (json.parent?.get("scale")?.asFloat() ?: 1f) to true
        }
    }
}
