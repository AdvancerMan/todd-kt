package io.github.advancerman.todd.box2d.bodyPattern.base

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import io.github.advancerman.todd.box2d.BodyFactory

open class PolygonBodyPattern(type: BodyDef.BodyType, worldCenter: Vector2, localVertices: Array<Vector2>) :
        SimpleBodyPattern(type, worldCenter) {
    protected val localVertices = localVertices.flatMap { listOf(it.x, it.y) }.toFloatArray()

    override fun addFixtures(body: Body) {
        BodyFactory.addPolygon(body, localVertices)
    }
}
