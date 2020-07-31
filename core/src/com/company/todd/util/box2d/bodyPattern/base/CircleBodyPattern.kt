package com.company.todd.util.box2d.bodyPattern.base

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.util.box2d.BodyFactory

class CircleBodyPattern(type: BodyDef.BodyType, val radius: Float,
                        worldCenter: Vector2 = Vector2(), val localCenter: Vector2 = Vector2()):
        SimpleBodyPattern(type, worldCenter) {
    override fun addFixtures(body: Body) {
        BodyFactory.addCircle(body, radius, localCenter)
    }
}
