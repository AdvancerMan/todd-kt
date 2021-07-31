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
    /**
     * Polygon body pattern with top ground sensor
     * @param b2dType Box2D body type
     * @param worldCenter World position for origin of body coordinate system
     * @param localVertices Body vertices in body coordinate system
     *                      listed in counter-clockwise order.
     *                      Polygon must be convex.
     *                      Number or vertices must be in range 3..8
     * @param scale Body scale relative to origin of body coordinate system.
     *              If not present is taken from parent json.
     */
    @SerializationType(BodyPattern::class, "PolygonWithTopGS")
    fun createPolygonBPWithTGS(
        b2dType: BodyDef.BodyType,
        worldCenter: Vector2 = Vector2(0f, 0f),
        localVertices: Array<Vector2>,
        scale: Float = 1f
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
    private fun manualCreatePolygonBPWithTGS(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        if (!parsed["localVertices"]!!.second) {
            parsed["localVertices"] = json["localVertices", vectorArray] to true
        }
        parseScale(json, parsed)
    }

    /**
     * Rectangle body pattern with top ground sensor
     * @param b2dType Box2D body type
     * @param worldPosition World position for lower left corner of the rectangle
     * @param size Rectangle width and height
     * @param localCenter Rectangle center relative to origin of body coordinate system
     * @param scale Body scale relative to origin of body coordinate system.
     *              If not present is taken from parent json.
     */
    @SerializationType(BodyPattern::class, "RectangleWithTopGS")
    fun createRectangleBPWithTGS(
        b2dType: BodyDef.BodyType,
        worldPosition: Vector2 = Vector2(0f, 0f),
        size: Vector2,
        localCenter: Vector2 = Vector2(0f, 0f),
        scale: Float = 1f
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
    private fun manualCreateRectangleBPWithTGS(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        parseScale(json, parsed)
    }

    /**
     * Rectangle body pattern with top ground and bottom ground sensors
     * @param b2dType Box2D body type
     * @param worldPosition World position for lower left corner of the rectangle
     * @param size Rectangle width and height
     * @param localCenter Rectangle center relative to origin of body coordinate system
     * @param scale Body scale relative to origin of body coordinate system.
     *              If not present is taken from parent json.
     */
    @SerializationType(BodyPattern::class, "RectangleWithTopGSBottomGS")
    fun createRectangleBPWithTGSBGS(
        b2dType: BodyDef.BodyType,
        worldPosition: Vector2 = Vector2(0f, 0f),
        size: Vector2,
        localCenter: Vector2 = Vector2(0f, 0f),
        scale: Float = 1f
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
    private fun manualCreateRectangleBPWithTGSBGS(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        parseScale(json, parsed)
    }

    /**
     * Circle body pattern
     * @param b2dType Box2D body type
     * @param worldCenter World position for origin of body coordinate system
     * @param radius Body radius
     * @param scale Body scale relative to origin of body coordinate system.
     *              If not present is taken from parent json.
     */
    @SerializationType(BodyPattern::class, "Circle")
    fun createCircleBP(
        b2dType: BodyDef.BodyType,
        worldCenter: Vector2 = Vector2(0f, 0f),
        radius: Float,
        scale: Float = 1f
    ): BodyPattern =
        CircleBodyPattern(b2dType, worldCenter, radius * scale)
            .withSerializer {
                it.addChild("type", "circle".toJsonValue())
                it.addChild("b2dType", b2dType.toJsonValue())
                it.addChild("worldCenter", worldCenter.toJsonValue())
                it.addChild("radius", radius.toJsonValue())
                it.addChild("scale", scale.toJsonValue())
            }

    @ManualJsonConstructor("createCircleBP")
    private fun manualCreateCircleBP(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        parseScale(json, parsed)
    }

    private fun parseScale(json: JsonValue, parsed: MutableMap<String, Pair<Any?, Boolean>>) {
        if (parsed["scale"]?.second != true) {
            parsed["scale"] = (json.parent?.get("scale")?.asFloat() ?: 1f) to true
        }
    }
}
