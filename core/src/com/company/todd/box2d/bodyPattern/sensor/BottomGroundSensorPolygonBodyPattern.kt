package com.company.todd.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.box2d.BodyFactory
import com.company.todd.box2d.bodyPattern.base.PolygonBodyPattern
import com.company.todd.box2d.bodyPattern.base.SensorName
import com.company.todd.box2d.bodyPattern.base.legsAngle

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

const val bottomSensorOffset = 1f
const val bottomSensorCuttingCoefficient = 0.9f

fun getBottomSensorPolygons(vertices: FloatArray) =
        List(vertices.size / 2) { Vector2(vertices[it * 2], vertices[it * 2 + 1]) }.let {
            it
                    .takeEdges()
                    .ifEmpty { listOf(it.minBy { e -> e.y }!!) }
                    .map { e -> e.cpy().sub(0f, bottomSensorOffset) }
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

            360 - legsAngle < edgeJ.angle()
                    || edgeJ.angle() < legsAngle
                    || 360 - legsAngle < edgeK.angle()
                    || edgeK.angle() < legsAngle
        }

private fun List<Vector2>.atLeast2() =
        if (size == 1) {
            listOf(
                    this[0],
                    Vector2(this[0].x + 1f, this[0].y - bottomSensorOffset),
                    Vector2(this[0].x - 1f, this[0].y - bottomSensorOffset)
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
                    .scl(bottomSensorCuttingCoefficient)
                    .add(this[j])
        }

private fun List<Vector2>.toEdges() =
        List(size - 1) {
            listOf(
                    this[it], this[it + 1],
                    this[it + 1].cpy().add(0f, 2 * bottomSensorOffset),
                    this[it].cpy().add(0f, 2 * bottomSensorOffset)
            )
        }
