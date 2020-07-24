package com.company.todd.objects.base

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.company.todd.launcher.ToddGame
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.AnimationType
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.asset.texture.MyDrawableI
import com.company.todd.util.asset.texture.TextureManager
import com.company.todd.util.box2d.bodyPattern.Sensor

private var maxID = 0

private fun getNewID() = maxID++

abstract class InGameObject(protected val game: ToddGame,
                            private val drawable: MyDrawable,
                            private val body: BodyWrapper):
        Group(), Disposable, Sensor, BodyWrapper, MyDrawableI {
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
            body.setOwner(this)
            body.getAABB().let { setSize(it.width, it.height) }
            setOrigin(Align.center)
            setScale(1f)
        }
    }

    override fun act(delta: Float) {
        drawable.update(delta)
        super.act(delta)
    }

    open fun postAct(delta: Float) {
        body.getCenter().let { setPosition(it.x, it.y, Align.center) }
        this.rotation = body.getAngle()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        // TODO [performance] cooling area for actor
        val batchColor = batch.color.cpy()
        batch.color = color.apply { a *= parentAlpha }
        drawable.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        color.a /= parentAlpha
        batch.color = batchColor
        super.draw(batch, parentAlpha)
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
        drawable.dispose(game.textureManager)
    }

    // delegating MyDrawable implementation to drawable
    override fun isDirectedToRight() = drawable.isDirectedToRight()
    override fun setDirectedToRight(directedToRight: Boolean) = drawable.setDirectedToRight(directedToRight)
    override fun setPlayingType(type: AnimationType, forceReset: Boolean) = drawable.setPlayingType(type, forceReset)
    override fun getPlayingType() = drawable.getPlayingType()

    final override fun update(delta: Float) {
        throw UnsupportedOperationException("To update IGO act(Float) should be called")
    }

    final override fun dispose(manager: TextureManager) {
        throw UnsupportedOperationException("To free IGO native resources dispose() should be called")
    }

    // delegating BodyWrapper implementation to body
    override fun applyLinearImpulseToCenter(impulse: Vector2) = body.applyLinearImpulseToCenter(impulse)
    override fun applyForceToCenter(force: Vector2) = body.applyForceToCenter(force)
    override fun isFixedRotation() = body.isFixedRotation()
    override fun getCenter() = body.getCenter()
    override fun getVelocity() = body.getVelocity()
    override fun getAngle() = body.getAngle()
    override fun setVelocity(v: Vector2) = body.setVelocity(v)
    override fun setCenter(x: Float, y: Float, resetLinearVelocity: Boolean) = body.setCenter(x, y, resetLinearVelocity)
    override fun setAngle(angle: Float, resetAngularVelocity: Boolean) = body.setAngle(angle, resetAngularVelocity)
    override fun setOwner(owner: InGameObject) = body.setOwner(owner)
    override fun getAABB() = body.getAABB()
    override fun isActive() = body.isActive()
    override fun setActive(value: Boolean) = body.setActive(value)

    final override fun destroy(world: World) {
        throw UnsupportedOperationException("To free IGO native resources dispose() should be called")
    }
}
