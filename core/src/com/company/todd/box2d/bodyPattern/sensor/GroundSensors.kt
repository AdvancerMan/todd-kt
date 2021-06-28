package com.company.todd.box2d.bodyPattern.sensor

import com.company.todd.objects.base.InGameObject

interface TopGroundListener {
    fun beginOnGround(obj: InGameObject) {}
    fun endOnGround(obj: InGameObject) {}
}

class TopGroundSensor(private val listener: TopGroundListener) : Sensor, TopGroundListener {
    override fun beginOnGround(obj: InGameObject) = listener.beginOnGround(obj)
    override fun endOnGround(obj: InGameObject) = listener.endOnGround(obj)
}
