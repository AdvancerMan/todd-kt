package com.company.todd.objects.base

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
import com.company.todd.launcher.ToddGame
import com.company.todd.screen.game.GameScreen
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.base.SensorName
import com.company.todd.box2d.bodyPattern.sensor.Sensor
import com.company.todd.box2d.bodyPattern.sensor.TopGroundListener
import com.company.todd.box2d.bodyPattern.sensor.TopGroundSensor
import com.company.todd.json.*

private var maxID = 0

private fun getNewID() = maxID++

abstract class InGameObject(
    protected val game: ToddGame, drawable: MyDrawable, drawableSize: Vector2?,
    @JsonFullSerializable private val bodyLowerLeftCornerOffset: Vector2,
    // TODO what if static body wrapper?
    @JsonUpdateSerializable("bodyPattern") val body: BodyWrapper
) : Group(), Disposable, Sensor, TopGroundListener, ManuallyJsonSerializable {
    private val drawableActor: DrawableActor = drawable.toDrawableActor().apply {
        this.setPosition(0f, 0f)
    }

    @JsonFullSerializable
    val drawable: MyDrawable
        get() = drawableActor.drawable!!

    // before init() it is drawableLowerLeftCornerOffset
    private val drawableCenterOffset = bodyLowerLeftCornerOffset.cpy()

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

        drawableSize?.let {
            setSize(it.x, it.y)
        }  ?: run {
            width = -1f
            height = -1f
        }
    }

    protected open fun doInit(gameScreen: GameScreen) {
        this.screen = gameScreen
        body.init(gameScreen)
        body.setOwner(this)

        val aabb = body.getUnrotatedAABB()
        if (width < 0 && height < 0) {
            setSize(aabb.width, aabb.height)
        }
        drawableCenterOffset
                .sub(aabb.width / 2, aabb.height / 2)
                .add(width / 2, height / 2)
        body.getCenter().add(drawableCenterOffset).let { setPosition(it.x, it.y, Align.center) }

        setScale(1f)
        this.rotation = MathUtils.radiansToDegrees * body.getAngle()

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
        val newPosition = body.getCenter().add(drawableCenterOffset)
        if (!isDirectedToRight) {
            newPosition.x -= drawableCenterOffset.x * 2
        }
        setPosition(newPosition.x, newPosition.y, Align.center)

        setOrigin(Align.center)
        originX -= drawableCenterOffset.x * if (isDirectedToRight) 1 else -1
        originY -= drawableCenterOffset.y

        this.rotation = MathUtils.radiansToDegrees * body.getAngle()
        updateColor()

        drawableActor.flipX = !isDirectedToRight
        drawableActor.flipY = false
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (getActorAABB().overlaps(screen.getCameraAABB())) {
            super.draw(batch, parentAlpha * color.a)
        }
    }

    override fun addActor(actor: Actor) {
        if (actor !is DrawableActor) {
            super.addActor(actor)
            return
        }

        val actorDrawable = actor.drawable!!
        val nextActor = children.firstOrNull {
            it is DrawableActor && it.drawable!!.zIndex > actorDrawable.zIndex
        }
        if (nextActor == null) {
            super.addActor(actor)
        } else {
            addActorBefore(nextActor, actor)
        }
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        drawableActor.setSize(width, height)
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

    @JsonFullSerializable("drawableSize")
    private fun getDrawableActorSize(): Vector2 {
        return Vector2(width, height)
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

    companion object {
        @ManualJsonConstructor
        fun getJsonConstructorDefaults(
            @Suppress("UNUSED_PARAMETER") json: JsonValue,
            parsed: MutableMap<String, Pair<Any?, Boolean>>
        ) {
            JsonDefaults.setDefault("bodyLowerLeftCornerOffset", Vector2(), parsed)
            JsonDefaults.setDefault("drawableSize", null, parsed)
        }
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

fun MyDrawable.toDrawableActor() =
    Pools.obtain(DrawableActor::class.java)!!.also { it.drawable = this }
