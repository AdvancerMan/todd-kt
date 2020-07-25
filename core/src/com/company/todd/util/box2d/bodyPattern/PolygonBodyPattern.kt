package com.company.todd.util.box2d.bodyPattern

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.util.box2d.BodyFactory

open class PolygonBodyPattern(localVertices: Array<Vector2>, type: BodyDef.BodyType, center: Vector2) :
        SimpleBodyPattern(type, center) {
    lateinit var localVertices: FloatArray private set

    init {
        setLocalVertices(localVertices)
    }

    fun setLocalVertices(vertices: Array<Vector2>) {
        localVertices = vertices.flatMap { listOf(it.x, it.y) }.toFloatArray()
    }

    override fun addFixtures(body: Body) {
        // TODO check polygon on vertices is convex
        BodyFactory.addPolygon(body, localVertices)
    }
}