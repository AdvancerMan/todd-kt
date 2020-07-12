package com.company.todd.util.box2d.bodyPattern

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.util.box2d.BodyFactory

class CircleBodyPattern(type: BodyDef.BodyType, var radius: Float,
                        centerPosition: Vector2 = Vector2()) :
        BodyPattern(type, centerPosition) {
    override fun createBody() =
            BodyFactory.createBody(world, type, position).apply {
                BodyFactory.addCircle(this, radius)
            }
}