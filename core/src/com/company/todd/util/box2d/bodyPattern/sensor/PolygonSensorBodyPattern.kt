package com.company.todd.util.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.util.box2d.BodyFactory
import com.company.todd.util.box2d.bodyPattern.base.PolygonBodyPattern
import com.company.todd.util.box2d.bodyPattern.base.SensorName

open class PolygonSensorBodyPattern(localVertices: Array<Vector2>, type: BodyDef.BodyType,
                                    center: Vector2, val sensorName: SensorName) :
        PolygonBodyPattern(localVertices, type, center) {

    override fun addFixtures(body: Body) {
        sensors[sensorName]?.let { sensor ->
            BodyFactory.addPolygon(body, localVertices).apply {
                userData = sensor
                isSensor = true
            }
        }
    }
}
