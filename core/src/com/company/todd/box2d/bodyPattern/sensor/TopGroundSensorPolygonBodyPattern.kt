package com.company.todd.box2d.bodyPattern.sensor

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.box2d.BodyFactory
import com.company.todd.box2d.bodyPattern.base.PolygonBodyPattern
import com.company.todd.box2d.bodyPattern.base.SensorName
import com.company.todd.util.mutate

open class TopGroundSensorPolygonBodyPattern(type: BodyDef.BodyType, worldCenter: Vector2, localVertices: Array<Vector2>) :
        PolygonBodyPattern(type, worldCenter, localVertices) {

    override fun addFixtures(body: Body) {
        // also multiplies x by -1 but it does not break the algorithm
        localVertices.mutate { it * -1 }
        sensors[SensorName.TOP_GROUND_SENSOR]?.let { sensor ->
            getBottomSensorPolygons(localVertices).forEach { polygons ->
                BodyFactory.addPolygon(body, polygons.mutate { it * -1 }).apply {
                    userData = sensor
                    isSensor = true
                }
            }
        }
        localVertices.mutate { it * -1 }
    }
}
