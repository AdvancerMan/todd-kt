package io.github.advancerman.todd.box2d.bodyPattern.base

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import io.github.advancerman.todd.util.SMOOTH_RECT_BOTTOM_ANGLE
import io.github.advancerman.todd.util.SMOOTH_RECT_MAX_SMOOTHNESS
import io.github.advancerman.todd.util.SMOOTH_RECT_SMOOTH_COEFFICIENT
import kotlin.math.tan

class RectangleBodyPattern(type: BodyDef.BodyType, worldPosition: Vector2,
                           size: Vector2, localCenter: Vector2 = Vector2()) :
        PolygonBodyPattern(
                type,
                worldPosition.cpy().add(size.x / 2, size.y / 2),
                createSmoothRectangle(localCenter, size)
        )

fun createSmoothRectangle(center: Vector2, size: Vector2): Array<Vector2> {
    val smoothRectTanBottomAngle = tan(SMOOTH_RECT_BOTTOM_ANGLE * MathUtils.degreesToRadians)
    val a = (size.x * SMOOTH_RECT_SMOOTH_COEFFICIENT)
            .coerceAtMost(size.y * SMOOTH_RECT_SMOOTH_COEFFICIENT / smoothRectTanBottomAngle)
            .coerceAtMost(SMOOTH_RECT_MAX_SMOOTHNESS)
    val b = a * smoothRectTanBottomAngle
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
