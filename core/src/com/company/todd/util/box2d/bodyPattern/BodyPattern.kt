package com.company.todd.util.box2d.bodyPattern

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.company.todd.util.box2d.BodyFactory

interface BodyPattern {
    fun addFixtures(body: Body)

    fun createBody(world: World, type: BodyDef.BodyType, center: Vector2) =
            BodyFactory.createBody(world, type, center).also { addFixtures(it) }
}

fun BodyPattern.combine(addMoreFixtures: (Body) -> Unit) = object : BodyPattern {
    override fun addFixtures(body: Body) {
        this@combine.addFixtures(body)
        addMoreFixtures(body)
    }
}

fun BodyPattern.combine(other: BodyPattern) = combine { other.addFixtures(it) }

abstract class SimpleBodyPattern(var type: BodyDef.BodyType, var center: Vector2) : BodyPattern {
    fun createBody(world: World) = createBody(world, type, center)
}
