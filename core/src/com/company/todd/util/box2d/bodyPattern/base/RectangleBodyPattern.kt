package com.company.todd.util.box2d.bodyPattern.base

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import kotlin.math.min

class RectangleBodyPattern(type: BodyDef.BodyType, worldPosition: Vector2,
                           size: Vector2, localCenter: Vector2 = Vector2()) :
        PolygonBodyPattern(
                type,
                worldPosition.cpy().add(size.x / 2, size.y / 2),
                createSmoothRectangle(localCenter, size)
        )

const val legsAngle = 61f
// tan(PI / 3)
const val tanLegsAngle = 1.7320508075688767f
const val smoothCoefficient = 0.1f
const val maxSmoothness = 3f

fun createSmoothRectangle(center: Vector2, size: Vector2): Array<Vector2> {
    val a = min(size.x * smoothCoefficient, size.y * smoothCoefficient / tanLegsAngle)
            .coerceAtMost(maxSmoothness)
    val b = a * tanLegsAngle
    val halfH = size.y / 2
    val halfW = size.x / 2

    return listOf(
            Vector2(halfW, halfH - b),
            Vector2(halfW - a, halfH),
            Vector2(a - halfW, halfH),
            Vector2(-halfW, halfH - b),
            Vector2(-halfW, b - halfH),
            Vector2(a - halfW, -halfH),
            Vector2(halfW - a, -halfH),
            Vector2(halfW, b - halfH)
    )
            .map { it.add(center) }
            .toTypedArray()
}
