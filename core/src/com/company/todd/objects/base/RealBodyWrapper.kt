package com.company.todd.objects.base

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2

const val pixInMeter = 30f

fun Float.toPix() = this * pixInMeter
fun Float.toMeters() = this / pixInMeter

fun Vector2.toPix() = this.scl(pixInMeter)
fun Vector2.toMeters() = this.scl(1 / pixInMeter)

fun Matrix4.toPix() = this.scl(pixInMeter)

class RealBodyWrapper {
}
