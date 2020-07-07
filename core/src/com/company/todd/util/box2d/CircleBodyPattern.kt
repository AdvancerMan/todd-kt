package com.company.todd.util.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World

class CircleBodyPattern(world: World, type: BodyDef.BodyType,
                        var radius: Float, position: Vector2 = Vector2()) :
        BodyPattern(world, type, position) {
    override fun createBody() =
            BodyFactory.createBody(world, type, position).apply {
                BodyFactory.addCircle(this, radius)
            }
}
