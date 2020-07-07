package com.company.todd.util.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World

abstract class BodyPattern(var world: World, var type: BodyDef.BodyType, var position: Vector2 = Vector2()) {
    abstract fun createBody(): Body
}
