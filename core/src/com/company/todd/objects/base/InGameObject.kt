package com.company.todd.objects.base

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.company.todd.launcher.ToddGame
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.animated.AnimationType
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.asset.texture.MyDrawableI
import com.company.todd.util.asset.texture.TextureManager
import com.company.todd.util.box2d.bodyPattern.sensor.Sensor
import com.company.todd.util.box2d.bodyPattern.sensor.TopGroundListener

private var maxID = 0

private fun getNewID() = maxID++

abstract class InGameObject(protected val game: ToddGame,
                            private val drawable: MyDrawable,
                            private val body: BodyWrapper,
                            drawableSize: Vector2, bodyLowerLeftCornerOffset: Vector2):
        Group(), Disposable, Sensor, BodyWrapper, MyDrawableI, TopGroundListener {
    // before init() it is drawableLowerLeftCornerOffset
    private val drawableCenterOffset = bodyLowerLeftCornerOffset.cpy().scl(-1f)
    private val id: Int = getNewID()
    var initialized = false
        private set
    protected lateinit var screen: GameScreen
    var alive = true
        private set
    var isDirectedToRight = true

    init {
        width = drawableSize.x
        height = drawableSize.y
    }

    protected open fun doInit(gameScreen: GameScreen) {
        this.screen = gameScreen
        body.init(gameScreen)
        body.setOwner(this)
        sizeChanged()

        val aabb = getAABB()
        drawableCenterOffset
                .sub(aabb.width / 2, aabb.height / 2)
                .add(width / 2, height / 2)
        getCenter().add(drawableCenterOffset).let { setPosition(it.x, it.y, Align.center) }

        setOrigin(Align.center)
        setScale(1f)
        this.rotation = MathUtils.radiansToDegrees * body.getAngle()
    }

    final override fun init(gameScreen: GameScreen) {
        if (!initialized) {
            initialized = true
            doInit(gameScreen)
        }
    }

    override fun act(delta: Float) {
        drawable.update(delta)
        super.act(delta)
    }

    open fun postAct(delta: Float) {
        getCenter().add(drawableCenterOffset).let { setPosition(it.x, it.y, Align.center) }
        this.rotation = MathUtils.radiansToDegrees * body.getAngle()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (getActorAABB().overlaps(screen.getCameraAABB())) {
            val batchColor = batch.color.cpy()
            batch.color = batch.color.mul(color).apply { a *= parentAlpha }
            drawable.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation, !isDirectedToRight, false)
            batch.color = batchColor
            super.draw(batch, parentAlpha)
        }
    }

    open fun takeDamage(amount: Float) {}

    override fun equals(other: Any?) =
            other is InGameObject && hashCode() == other.hashCode()

    override fun hashCode() = id

    fun kill() {
        alive = false
    }

    override fun dispose() {
        if (initialized) {
            screen.destroyBody(body)
        }
        drawable.dispose(game.textureManager)
    }

    // delegating MyDrawable implementation to drawable
    override fun setPlayingType(type: AnimationType, forceReset: Boolean) = drawable.setPlayingType(type, forceReset)
    override fun getPlayingType() = drawable.getPlayingType()
    override fun isAnimationFinished() = drawable.isAnimationFinished()

    final override fun update(delta: Float) {
        Gdx.app.error("IGO", "To update IGO act(Float) should be called")
    }

    final override fun dispose(manager: TextureManager) {
        Gdx.app.error("IGO", "To free IGO native resources dispose() should be called")
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
        Gdx.app.error("IGO", "To free IGO native resources dispose() should be called")
    }
}

fun Actor.getActorAABB() =
        worldAABBFor(Rectangle(0f, 0f, width, height))

fun Actor.worldAABBFor(rectangle: Rectangle) =
        rectangle.apply {
            listOf(
                    Vector2(x, y),
                    Vector2(x + width, y),
                    Vector2(x, y + height),
                    Vector2(x + width, y + height)
            )
                    .map { localToStageCoordinates(it) }
                    .also { set(it[0].x, it[0].y, 0f, 0f) }
                    .fold(this) { r, v -> r.merge(v) }
        }

fun Rectangle.scale(originX: Float, originY: Float, scaleX: Float, scaleY: Float) =
        setPosition(x - originX, y - originY)
                .set(x * scaleX, y * scaleY, width * scaleX, height * scaleY)
                .setPosition(x + originX, y + originY)!!
