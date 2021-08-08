package io.github.advancerman.todd.box2d

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import io.github.advancerman.todd.objects.base.toMeters
import io.github.advancerman.todd.util.DEFAULT_DENSITY
import io.github.advancerman.todd.util.DEFAULT_FRICTION
import io.github.advancerman.todd.util.DEFAULT_LINEAR_DAMPING
import io.github.advancerman.todd.util.DEFAULT_RESTITUTION

object BodyFactory {
    private val bodyDef = BodyDef()
    private val fixtureDef = FixtureDef()

    fun createBody(world: World, type: BodyDef.BodyType, center: Vector2,
                   fixedRotation: Boolean = true, isBullet: Boolean = false) =
            world.createBody(bodyDef.apply {
                this.type = type
                this.position.set(center).toMeters()
                this.fixedRotation = fixedRotation
                this.bullet = isBullet
                this.linearDamping = DEFAULT_LINEAR_DAMPING
            })!!

    private fun createFixture(body: Body, shape_: Shape,
                              density_: Float, friction_: Float, restitution_: Float) =
            body.createFixture(fixtureDef.apply {
                density = density_
                friction = friction_
                restitution = restitution_
                shape = shape_
            })!!

    fun addBox(body: Body, width: Float, height: Float, center: Vector2 = Vector2(),
               density: Float = DEFAULT_DENSITY, friction: Float = DEFAULT_FRICTION,
               restitution: Float = DEFAULT_RESTITUTION, angle: Float = 0f) =
            PolygonShape().let {
                it.setAsBox(width.toMeters() / 2, height.toMeters() / 2, center.cpy().toMeters(), angle)
                val res = createFixture(body, it, density, friction, restitution)
                it.dispose()
                res
            }

    /**
     * Vertices are in counter-clockwise order
     * Maximum vertices count = 8
     * @see b2_maxPolygonVertices
     */
    fun addPolygon(body: Body, vertices: FloatArray, density: Float = DEFAULT_DENSITY,
                   friction: Float = DEFAULT_FRICTION, restitution: Float = DEFAULT_RESTITUTION) =
            PolygonShape().let {
                it.set(vertices.map { e -> e.toMeters() }.toFloatArray())
                val res = createFixture(body, it, density, friction, restitution)
                it.dispose()
                res
            }

    fun addCircle(body: Body, radius: Float, center: Vector2 = Vector2(), density: Float = DEFAULT_DENSITY,
                  friction: Float = DEFAULT_FRICTION, restitution: Float = DEFAULT_RESTITUTION) =
            CircleShape().let {
                it.position = center
                it.position.toMeters()
                it.radius = radius.toMeters()

                val res = createFixture(body, it, density, friction, restitution)
                it.dispose()
                res
            }

    fun addEdge(body: Body, x1: Float, y1: Float, x2: Float, y2: Float,
                density: Float = DEFAULT_DENSITY, friction: Float = DEFAULT_FRICTION,
                restitution: Float = DEFAULT_RESTITUTION) =
            EdgeShape().let {
                it.set(x1.toMeters(), y1.toMeters(), x2.toMeters(), y2.toMeters())
                val res = createFixture(body, it, density, friction, restitution)
                it.dispose()
                res
            }

    fun addChain(body: Body, vertices: FloatArray, density: Float = DEFAULT_DENSITY,
                 friction: Float = DEFAULT_FRICTION, restitution: Float = DEFAULT_RESTITUTION) =
            ChainShape().let {
                it.createChain(vertices.map { e -> e.toMeters() }.toFloatArray())
                val res = createFixture(body, it, density, friction, restitution)
                it.dispose()
                res
            }


    fun addLoopChain(body: Body, vertices: FloatArray, density: Float = DEFAULT_DENSITY,
                     friction: Float = DEFAULT_FRICTION, restitution: Float = DEFAULT_RESTITUTION) =
            ChainShape().let {
                it.createLoop(vertices.map { e -> e.toMeters() }.toFloatArray())
                val res = createFixture(body, it, density, friction, restitution)
                it.dispose()
                res
            }
}
