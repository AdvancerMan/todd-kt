package com.company.todd.util.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World

class RectangleBodyPattern(world: World, type: BodyDef.BodyType,
                           var size: Vector2, position: Vector2 = Vector2()) :
        BodyPattern(world, type, position) {
    override fun createBody() =
            BodyFactory.createBody(world, type, position.cpy().add(size.x / 2, size.y / 2)).apply {
                BodyFactory.addBox(this, size.x, size.y)
            }
}