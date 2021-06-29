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
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.ToddGame
import com.company.todd.screen.GameScreen
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.MyDrawableI
import com.company.todd.asset.texture.TextureManager
import com.company.todd.box2d.bodyPattern.base.SensorName
import com.company.todd.box2d.bodyPattern.sensor.Sensor
import com.company.todd.box2d.bodyPattern.sensor.TopGroundListener
import com.company.todd.box2d.bodyPattern.sensor.TopGroundSensor
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.JsonUpdateSerializable
import com.company.todd.json.ManuallyJsonSerializable

private var maxID = 0

private fun getNewID() = maxID++

abstract class InGameObject(protected val game: ToddGame, private val drawable: MyDrawable,
                            drawableSize: Vector2, @JsonFullSerializable private val bodyLowerLeftCornerOffset: Vector2,
                            @JsonUpdateSerializable("bodyPattern") private val body: BodyWrapper) :
    Group(), Disposable, Sensor, BodyWrapper by body, MyDrawableI by drawable,
    TopGroundListener, ManuallyJsonSerializable {
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
        width = drawableSize.x
        height = drawableSize.y
    }

    protected open fun doInit(gameScreen: GameScreen) {
        this.screen = gameScreen
        body.init(gameScreen)
        body.setOwner(this)
        sizeChanged()

        val aabb = getUnrotatedAABB()
        drawableCenterOffset
                .sub(aabb.width / 2, aabb.height / 2)
                .add(width / 2, height / 2)
        getCenter().add(drawableCenterOffset).let { setPosition(it.x, it.y, Align.center) }

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

    open fun updateColor() {
        color.set(1f, 1f, 1f, 1f)
    }

    open fun postAct(delta: Float) {
        val newPosition = getCenter().add(drawableCenterOffset)
        if (!isDirectedToRight) {
            newPosition.x -= drawableCenterOffset.x * 2
        }
        setPosition(newPosition.x, newPosition.y, Align.center)

        setOrigin(Align.center)
        originX -= drawableCenterOffset.x * if (isDirectedToRight) 1 else -1
        originY -= drawableCenterOffset.y

        this.rotation = MathUtils.radiansToDegrees * body.getAngle()
        updateColor()
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

    // update from MyDrawable interface
    final override fun update(delta: Float) {
        Gdx.app.error("IGO", "To update IGO act(Float) should be called")
    }

    // dispose from MyDrawable interface
    final override fun dispose(manager: TextureManager) {
        Gdx.app.error("IGO", "To free IGO native resources dispose() should be called")
    }

    // destroy from BodyWrapper interface
    final override fun destroy(world: World) {
        Gdx.app.error("IGO", "To free IGO native resources dispose() should be called")
    }

    @JsonFullSerializable("drawableSize")
    private fun getActorDrawableSize(): Vector2 {
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
