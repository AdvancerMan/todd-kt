package com.company.todd.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.box2d.BodyFactory
import com.company.todd.box2d.bodyPattern.base.PolygonBodyPattern
import com.company.todd.box2d.bodyPattern.base.SensorName

open class PolygonSensorBodyPattern(type: BodyDef.BodyType, worldCenter: Vector2,
                                    val sensorName: SensorName, localVertices: Array<Vector2>) :
        PolygonBodyPattern(type, worldCenter, localVertices) {

    override fun addFixtures(body: Body) {
        sensors[sensorName]?.let { sensor ->
            BodyFactory.addPolygon(body, localVertices).apply {
                userData = sensor
                isSensor = true
            }
        }
    }
}
