package com.company.todd.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.box2d.BodyFactory
import com.company.todd.box2d.bodyPattern.base.PolygonBodyPattern
import com.company.todd.box2d.bodyPattern.base.SensorName
import com.company.todd.util.BOTTOM_GROUND_ANGLE
import com.company.todd.util.BOTTOM_SENSOR_CUTTING_COEFFICIENT
import com.company.todd.util.BOTTOM_SENSOR_OFFSET

open class BottomGroundSensorPolygonBodyPattern(type: BodyDef.BodyType, worldCenter: Vector2, localVertices: Array<Vector2>) :
        PolygonBodyPattern(type, worldCenter, localVertices) {

    override fun addFixtures(body: Body) {
        sensors[SensorName.BOTTOM_GROUND_SENSOR]?.let { sensor ->
            getBottomSensorPolygons(localVertices).forEach {
                BodyFactory.addPolygon(body, it).apply {
                    userData = sensor
                    isSensor = true
                }
            }
        }
    }
}

fun getBottomSensorPolygons(vertices: FloatArray) =
        List(vertices.size / 2) { Vector2(vertices[it * 2], vertices[it * 2 + 1]) }.let {
            it
                    .takeEdges()
                    .ifEmpty { listOf(it.minByOrNull { e -> e.y }!!) }
                    .map { e -> e.cpy().sub(0f, BOTTOM_SENSOR_OFFSET) }
                    .atLeast2()
                    .shiftToMakeNonCyclic()
                    .cutCorners()
                    .toEdges()
                    .map { edge -> edge.flatMap { e -> listOf(e.x, e.y) }.toFloatArray() }
        }

// taking edges with [-legsAngle, legsAngle] angle in degrees
private fun List<Vector2>.takeEdges() =
        filterIndexed { i, v ->
            val j = (i + 1) % size
            val k = (i - 1 + size) % size
            val edgeJ = this[j].cpy().sub(v)
            val edgeK = v.cpy().sub(this[k])

            360 - BOTTOM_GROUND_ANGLE < edgeJ.angle()
                    || edgeJ.angle() < BOTTOM_GROUND_ANGLE
                    || 360 - BOTTOM_GROUND_ANGLE < edgeK.angle()
                    || edgeK.angle() < BOTTOM_GROUND_ANGLE
        }

private fun List<Vector2>.atLeast2() =
        if (size == 1) {
            listOf(
                    this[0],
                    Vector2(this[0].x + 1f, this[0].y - BOTTOM_SENSOR_OFFSET),
                    Vector2(this[0].x - 1f, this[0].y - BOTTOM_SENSOR_OFFSET)
            )
        } else {
            this
        }

private fun List<Vector2>.shiftToMakeNonCyclic(): List<Vector2> {
    var leftCornerI = 0
    for (i in 0 until size) {
        if (this[i].x < this[leftCornerI].x || this[i].y > this[leftCornerI].y) {
            leftCornerI = i
        }
    }

    return List(size) { this[(it + leftCornerI) % size] }
}

// cutting corners to fix wall jumping problem
private fun List<Vector2>.cutCorners() =
        cutCorners(0, 1).cutCorners(size - 1, size - 2)

private fun List<Vector2>.cutCorners(i: Int, j: Int) =
        apply {
            this[i]
                    .sub(this[j])
                    .scl(BOTTOM_SENSOR_CUTTING_COEFFICIENT)
                    .add(this[j])
        }

private fun List<Vector2>.toEdges() =
        List(size - 1) {
            listOf(
                    this[it], this[it + 1],
                    this[it + 1].cpy().add(0f, 2 * BOTTOM_SENSOR_OFFSET),
                    this[it].cpy().add(0f, 2 * BOTTOM_SENSOR_OFFSET)
            )
        }
