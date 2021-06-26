package com.company.todd.box2d.bodyPattern.base

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.company.todd.box2d.BodyFactory
import com.company.todd.box2d.bodyPattern.sensor.Sensor

enum class SensorName {
    BOTTOM_GROUND_SENSOR, TOP_GROUND_SENSOR
}

/**
 * @property sensors objects that are set as Fixture.userObject in BodyPattern::addFixtures.
 * Sensors are not necessarily used and can be modified by BodyPattern::combine extension function
 */
interface BodyPattern {
    var sensors: MutableMap<SensorName, Sensor>
    fun addFixtures(body: Body)
    fun createBody(world: World): Body
}

fun BodyPattern.combine(other: BodyPattern) =
        object : BodyPattern {
            override var sensors = other.sensors
                    .toMutableMap()
                    .apply { this@combine.sensors.forEach { (name, sensor) -> put(name, sensor) } }

            override fun createBody(world: World) =
                    this@combine
                            .also { it.sensors = sensors }
                            .createBody(world)
                            .also {
                                other.sensors = sensors
                                other.addFixtures(it)
                            }

            override fun addFixtures(body: Body) {
                listOf(this@combine, other).forEach {
                    it.sensors = sensors
                    it.addFixtures(body)
                }
            }
        }

abstract class SimpleBodyPattern(val type: BodyDef.BodyType, val worldCenter: Vector2) : BodyPattern {
    override var sensors = mutableMapOf<SensorName, Sensor>()

    override fun createBody(world: World) =
            BodyFactory.createBody(world, type, worldCenter).also { addFixtures(it) }
}