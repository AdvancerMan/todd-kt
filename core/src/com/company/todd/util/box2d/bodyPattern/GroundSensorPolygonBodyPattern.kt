package com.company.todd.util.box2d.bodyPattern

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.util.box2d.BodyFactory

open class GroundSensorPolygonBodyPattern(localVertices: Array<Vector2>, type: BodyDef.BodyType, center: Vector2) :
        PolygonBodyPattern(localVertices, type, center), GroundSensorBodyPattern {
    override var groundSensor: Sensor? = null

    override fun addFixtures(body: Body) {
        super.addFixtures(body)

        if (groundSensor != null) {
            BodyFactory.addPolygon(body, getGroundSensorPolygon(localVertices)).apply {
                userData = groundSensor
                isSensor = true
            }
        }
    }
}

const val groundSensorOffset = 2f
const val groundSensorCuttingCoefficient = 0.9f

private fun getGroundSensorPolygon(vertices: FloatArray) =
        List(vertices.size / 2) { Vector2(vertices[it * 2], vertices[it * 2 + 1]) }.let {
            it
                    .takeEdges()
                    .ifEmpty { listOf(it.minBy { e -> e.y }!!) }
                    .map { e -> e.cpy().sub(0f, groundSensorOffset) }
                    .atLeast3()
                    .cutCorners()
                    .flatMap { e -> listOf(e.x, e.y) }
                    .toFloatArray()
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

private fun List<Vector2>.atLeast3() =
        when (size) {
            2 -> listOf(
                    this[0], this[1],
                    Vector2(this[0].x, this[0].y - groundSensorOffset),
                    Vector2(this[1].x, this[1].y - groundSensorOffset)
            )

            1 -> listOf(
                    this[0],
                    Vector2(this[0].x + 1f, this[0].y - groundSensorOffset),
                    Vector2(this[0].x - 1f, this[0].y - groundSensorOffset)
            )

            else -> this
        }

// cutting corners to fix wall jumping problem
private fun List<Vector2>.cutCorners(): List<Vector2> {
    var leftCornerI = 0
    var rightCornerI = 0

    for (i in 0 until size) {
        if (this[i].x < this[leftCornerI].x || this[i].y > this[leftCornerI].y) {
            leftCornerI = i
        }
        if (this[i].x > this[rightCornerI].x || this[i].y > this[rightCornerI].y) {
            rightCornerI = i
        }
    }

    return cutCorners(leftCornerI, (leftCornerI + 1) % size)
            .cutCorners(rightCornerI, (rightCornerI + size - 1) % size)
}

private fun List<Vector2>.cutCorners(i: Int, j: Int) =
        apply {
            this[i]
                    .sub(this[j])
                    .scl(groundSensorCuttingCoefficient)
                    .add(this[j])
        }
