package io.github.advancerman.todd.box2d.bodyPattern.base

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import io.github.advancerman.todd.box2d.BodyFactory

class CircleBodyPattern(type: BodyDef.BodyType, worldCenter: Vector2,
                        val radius: Float, val localCenter: Vector2 = Vector2()):
        SimpleBodyPattern(type, worldCenter) {
    override fun addFixtures(body: Body) {
        BodyFactory.addCircle(body, radius, localCenter)
    }
}
