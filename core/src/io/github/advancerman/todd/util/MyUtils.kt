package io.github.advancerman.todd.util

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

fun Vector2.mirrorIf(
    shouldMirror: Boolean,
    originX: Float = 0f,
    addXIfMirrored: Float = 0f
): Vector2 {
    return if (!shouldMirror) {
        this
    } else {
        sub(originX, 0f).scl(-1f, 1f).add(originX + addXIfMirrored, 0f)
    }
}

fun Rectangle.scale(originX: Float, originY: Float, scaleX: Float, scaleY: Float) =
    setPosition(x - originX, y - originY)
        .set(x * scaleX, y * scaleY, width * scaleX, height * scaleY)
        .setPosition(x + originX, y + originY)!!


fun Rectangle.translate(trX: Float, trY: Float) =
    setPosition(x + trX, y + trY)!!

fun Rectangle.translate(v: Vector2) =
    translate(v.x, v.y)

fun Rectangle.rotate(angle: Float) =
    rotateRad(angle * MathUtils.degreesToRadians)

fun Rectangle.rotateRad(angleRad: Float) =
    listOf(
        Vector2(x, y),
        Vector2(x + width, y),
        Vector2(x, y + height),
        Vector2(x + width, y + height)
    )
        .map { it.rotateRad(angleRad) }
        .also { this.set(it[0].x, it[0].y, 0f, 0f) }
        .fold(this) { r, v -> r.merge(v) }

fun Rectangle.rotateAround(originX: Float, originY: Float, angle: Float) =
    rotateAroundRad(originX, originY, angle * MathUtils.degreesToRadians)

fun Rectangle.rotateAroundRad(originX: Float, originY: Float, angleRad: Float) =
    setPosition(x - originX, y - originY).rotateRad(angleRad).setPosition(x + originX, y + originY)!!

fun Rectangle.rotateAround(origin: Vector2, angle: Float) =
    rotateAround(origin.x, origin.y, angle)

fun Rectangle.rotateAroundRad(origin: Vector2, angleRad: Float) =
    rotateAroundRad(origin.x, origin.y, angleRad)

fun Rectangle.coordinateDistanceTo(other: Rectangle): Float {
    return if (overlaps(other)) {
        0f
    } else {
        maxOf(
            x - other.x - other.width,
            other.x - x - width,
            y - other.y - other.height,
            other.y - y - height,
        )
    }
}

fun <K, V> MutableMap<K, V>.putAll(vararg pairs: Pair<K, V>) = putAll(pairs)

inline fun FloatArray.mutate(f: (Float) -> Float) =
    apply { forEachIndexed { i, x -> this[i] = f(x) } }

fun <T> MutableCollection<T>.synchronizedFlush() = synchronized(this) {
    val result = toList()
    clear()
    result
}

fun ByteArray.contentEquals(offset: Int, length: Int, other: ByteArray) = asList()
    .subList(offset, offset + length)
    .toByteArray()
    .contentEquals(other)

inline fun <T> MutableIterable<T>.removeWhile(predicate: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.next()
        if (!predicate(element)) {
            break
        }
        iterator.remove()
        result.add(element)
    }
    return result
}

fun Thread.withExceptionHandler(handler: Thread.UncaughtExceptionHandler): Thread {
    val previousHandler = uncaughtExceptionHandler
    uncaughtExceptionHandler = previousHandler?.let {
        Thread.UncaughtExceptionHandler { thread, throwable ->
            try {
                it.uncaughtException(thread, throwable)
            } finally {
                handler.uncaughtException(thread, throwable)
            }
        }
    } ?: handler
    return this
}
