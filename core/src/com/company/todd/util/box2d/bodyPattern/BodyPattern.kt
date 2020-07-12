package com.company.todd.util.box2d.bodyPattern

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World

abstract class BodyPattern(var type: BodyDef.BodyType, val position: Vector2 = Vector2()) {
    lateinit var world: World
    abstract fun createBody(): Body
}
