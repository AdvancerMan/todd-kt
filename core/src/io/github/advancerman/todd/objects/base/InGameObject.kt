package io.github.advancerman.todd.objects.base

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.Pools
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.screen.game.GameScreen
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.asset.texture.WithZIndex
import io.github.advancerman.todd.asset.texture.animated.AnimationEvent
import io.github.advancerman.todd.box2d.bodyPattern.base.SensorName
import io.github.advancerman.todd.box2d.bodyPattern.sensor.Sensor
import io.github.advancerman.todd.box2d.bodyPattern.sensor.TopGroundListener
import io.github.advancerman.todd.box2d.bodyPattern.sensor.TopGroundSensor
import io.github.advancerman.todd.json.*
import io.github.advancerman.todd.util.mirrorIf

private var maxID = 0

private fun getNewID() = maxID++

abstract class InGameObject(
    protected val game: ToddGame, drawable: ToddDrawable,
    // TODO what if static body wrapper?
    @JsonUpdateSerializable("bodyPattern") val body: BodyWrapper,
    scale: Float
) : Group(), Disposable, Sensor, TopGroundListener, ManuallyJsonSerializable {
    private val drawableActor: DrawableActor = drawable.toDrawableActor().apply {
        this.setPosition(0f, 0f)
    }

    @JsonFullSerializable
    val drawable: ToddDrawable
        get() = drawableActor.drawable!!

    @JsonUpdateSerializable
    var id: Int = getNewID()

    var initialized = false
        private set

    protected lateinit var screen: GameScreen

    var alive = true
        private set

    @JsonUpdateSerializable
    var isDirectedToRight = true

    init {
        // it is guaranteed that link to this is not used by sensor while this creates
        @Suppress("LeakingThis")
        body.putSensor(SensorName.TOP_GROUND_SENSOR, TopGroundSensor(this))

        setSize(drawable.size.x, drawable.size.y)
        super.setScale(scale)
    }

    protected open fun doInit(gameScreen: GameScreen) {
        this.screen = gameScreen
        body.init(gameScreen)
        body.setOwner(this)

        if (width == 0f && height == 0f) {
            val aabb = body.getUnrotatedAABB()
            setSize(aabb.width, aabb.height)
        }

        addActor(drawableActor)
    }

    fun init(gameScreen: GameScreen) {
        if (!initialized) {
            initialized = true
            doInit(gameScreen)
        }
    }

    override fun act(delta: Float) {
        children.filterIsInstance<DrawableActor>().forEach { it.drawable!!.update(delta) }
        super.act(delta)
    }

    open fun updateColor() {
        color.set(1f, 1f, 1f, 1f)
    }

    open fun postAct(delta: Float) {
        val aabb = body.getUnrotatedAABB()
        val centerOffset = drawable.offset.cpy()
            .add(width / 2, height / 2)
            .sub(aabb.width / 2 / scaleX, aabb.height / 2 / scaleY)
            .mirrorIf(!isDirectedToRight, 0f)
        body.getCenter().add(centerOffset).let { setPosition(it.x, it.y, Align.center) }

        setOrigin(Align.center)
        originX -= centerOffset.x
        originY -= centerOffset.y

        this.rotation = MathUtils.radiansToDegrees * body.getAngle()
        updateColor()
        drawableActor.color = color

        drawableActor.flipX = !isDirectedToRight
        drawableActor.flipY = false
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (getActorAABB().overlaps(screen.getCameraAABB())) {
            super.draw(batch, parentAlpha * color.a)
        }
    }

    override fun addActor(actor: Actor) {
        if (actor !is WithZIndex) {
            super.addActor(actor)
            return
        }

        val nextActor = children.firstOrNull {
            it is WithZIndex && it.myZIndex > actor.myZIndex
        }
        if (nextActor == null) {
            super.addActor(actor)
        } else {
            addActorBefore(nextActor, actor)
        }
    }

    open fun reportAnimationEvent(animationEvent: AnimationEvent) {
        drawable.reportEvent(animationEvent)
    }

    final override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        drawableActor.setSize(width, height)
    }

    final override fun setPosition(x: Float, y: Float) {
        super.setPosition(x, y)
    }

    @JsonFullSerializable("scale")
    override fun getScaleX(): Float {
        return super.getScaleX()
    }

    final override fun setScale(scaleXY: Float) {
        throw UnsupportedOperationException("Scale for InGameObject is immutable")
    }

    final override fun setScaleX(scaleX: Float) {
        setScale(scaleX)
    }

    final override fun setScaleY(scaleY: Float) {
        setScale(scaleY)
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
        drawableActor.drawable = null
        Pools.free(drawableActor)
    }

    override fun deserializeUpdates(json: JsonValue) {
        // no operations
    }

    override fun serializeUpdates(json: JsonValue) {
        // no operations
    }

    override fun serializeFull(json: JsonValue) {
        // no operations
    }

    override fun serializeSave(json: JsonValue) {
        // no operations
    }
}

fun Actor.getActorAABB() =
    worldAABBFor(Rectangle(0f, 0f, width, height))

fun Actor.worldAABBFor(rectangle: Rectangle) =
    rectangle.let {
        listOf(
            Vector2(it.x, it.y),
            Vector2(it.x + it.width, it.y),
            Vector2(it.x, it.y + it.height),
            Vector2(it.x + it.width, it.y + it.height)
        )
    }
        .map {
            it.mirrorIf(this is InGameObject && !isDirectedToRight, width / 2)
        }
        .map { localToStageCoordinates(it) }
        .also { rectangle.set(it[0].x, it[0].y, 0f, 0f) }
        .fold(rectangle) { r, v -> r.merge(v) }

fun ToddDrawable.toDrawableActor() =
    Pools.obtain(DrawableActor::class.java)!!
        .also {
            it.drawable = this
            it.setPosition(0f, 0f)
            it.setOrigin(0f, 0f)
            it.setSize(0f, 0f)
            it.setScale(1f)
            it.rotation = 0f
        }
