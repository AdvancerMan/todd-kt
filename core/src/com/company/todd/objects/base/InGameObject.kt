package com.company.todd.objects.base

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Disposable
import com.company.todd.launcher.ToddGame
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.MySprite
import com.company.todd.util.box2d.bodyPattern.Sensor

private var maxID = 0

private fun getNewID() = maxID++

abstract class InGameObject(protected val game: ToddGame, protected val sprite: MySprite,
                            private val body: BodyWrapper): Group(), Disposable, Sensor, BodyWrapper {
    private val id: Int = getNewID()
    var initialized = false
        private set
    protected lateinit var screen: GameScreen
    var alive = true
        private set

    override fun init(gameScreen: GameScreen) {
        if (!initialized) {
            initialized = true
            this.screen = gameScreen
            body.init(gameScreen)
            sprite.rotation = body.getAngle()
            body.setOwner(this)
        }
    }

    override fun act(delta: Float) {
        sprite.update(delta)
        super.act(delta)
    }

    open fun postAct(delta: Float) {
        if (!body.isFixedRotation()) {
            sprite.rotation = body.getAngle()
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        // TODO [performance] cooling area for actor
        sprite.setAlpha(parentAlpha * color.a)
        sprite.draw(body.getCenter(), batch)
        super.draw(batch, parentAlpha)
    }

    fun isDirectedToRight() = sprite.isDirectedToRight

    fun setDirectedToRight(value: Boolean) {
        sprite.isDirectedToRight = value
    }

    open fun isGroundFor(other: InGameObject) = true
    open fun takeDamage(amount: Float) {}

    override fun equals(other: Any?) =
            other is InGameObject && hashCode() == other.hashCode()

    override fun hashCode() = id

    fun kill() {
        alive = false
    }

    override fun dispose() {
        if (initialized) {
            body.destroy(screen.world)
        }
        sprite.dispose(game.textureManager)
    }

    // delegating BodyWrapper implementation to body
    final override fun applyLinearImpulseToCenter(impulse: Vector2) = body.applyLinearImpulseToCenter(impulse)
    final override fun applyForceToCenter(force: Vector2) = body.applyForceToCenter(force)
    final override fun isFixedRotation() = body.isFixedRotation()
    final override fun getCenter() = body.getCenter()
    final override fun getVelocity() = body.getVelocity()
    final override fun getAngle() = body.getAngle()
    final override fun setVelocity(v: Vector2) = body.setVelocity(v)
    final override fun setCenter(x: Float, y: Float, resetLinearVelocity: Boolean) = body.setCenter(x, y, resetLinearVelocity)
    final override fun setAngle(angle: Float, resetAngularVelocity: Boolean) = body.setAngle(angle, resetAngularVelocity)
    final override fun setOwner(owner: InGameObject) = body.setOwner(owner)
    final override fun getAABB() = body.getAABB()
    final override fun isActive() = body.isActive()
    final override fun setActive(value: Boolean) = body.setActive(value)

    final override fun destroy(world: World) {
        throw UnsupportedOperationException("To free IGO native resources dispose() should be called")
    }
}
