package com.company.todd.util.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Disposable
import com.company.todd.launcher.ToddGame
import com.company.todd.util.*

enum class MovingInputType(val i: Int) {
    SLIDER(0), TOUCHPAD(1)
}

class PlayerInputActor(val game: ToddGame) : Group(), Disposable {
    var isMovingLeft = false
        private set
    var isMovingRight = false
        private set
    var isJumping = false
        private set
        get() = jumpButton.isPressed || field

    private val resources = listOf(
            "moveSliderBackground",
            "moveSliderKnob",
            "moveTouchpadBackground",
            "moveTouchpadKnob",
            "jumpButtonUp",
            "jumpButtonDown"
    ).map { game.textureManager.loadSprite(it) }

    private val movingActors = listOf(
            Slider(
                    0f, 100f, 1f, false,
                    Slider.SliderStyle(resources[0], resources[1])
            ).apply {
                width = MOVING_INPUT_SLIDER_WIDTH
                height = MOVING_INPUT_SLIDER_HEIGHT
                value = (maxValue + minValue) / 2
                userObject = MovingInputType.SLIDER
            },

            Touchpad(
                    0f,
                    Touchpad.TouchpadStyle(resources[2], resources[3])
            ).apply {
                width = MOVING_INPUT_TOUCHPAD_WIDTH
                height = MOVING_INPUT_TOUCHPAD_HEIGHT
                userObject = MovingInputType.TOUCHPAD
            }
    )

    private var inputActorIndex = MOVING_INPUT_DEFAULT_ACTOR_INDEX
        set(value) {
            resetMovingActor()
            movingActors[field].isVisible = false
            movingActors[value].isVisible = true
            field = value
        }

    private val jumpButton = Button(resources[4], resources[5]).apply {
        width = JUMP_BUTTON_WIDTH
        height = JUMP_BUTTON_HEIGHT
        setMyClickListener(this)
    }

    init {
        movingActors.forEach {
            addActor(it)
            it.isVisible = false
        }
        movingActors[inputActorIndex].isVisible = true
        addListener(createChangeListener())

        addActor(jumpButton)
        updatePosition()
    }

    fun setInputActor(type: MovingInputType) {
        inputActorIndex = type.i
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        setPosition(stage.camera.position.x - stage.camera.viewportWidth / 2,
                stage.camera.position.y - stage.camera.viewportHeight / 2)
        super.draw(batch, parentAlpha)
    }

    private fun resetMovingActor() {
        isMovingLeft = false
        isMovingRight = false
        isJumping = false

        val actor = movingActors[inputActorIndex]
        when (MovingInputType.values()[inputActorIndex]) {
            MovingInputType.SLIDER -> {
                actor as Slider
                actor.value = (actor.maxValue + actor.minValue) / 2
            }

            else -> {}
        }
    }

    fun resize(width: Float, height: Float) {
        setSize(width, height)
        updatePosition()
    }

    private fun calculatePosition(pos: Pair<Float, Float>) =
            Vector2((width + pos.first) % width, (height + pos.second) % height)

    private fun updatePosition() {
        calculatePosition(JUMP_BUTTON_POSITION).let { jumpButton.setPosition(it.x, it.y) }
        calculatePosition(MOVING_INPUT_SLIDER_POSITION).let { movingActors[0].setPosition(it.x, it.y) }
        calculatePosition(MOVING_INPUT_TOUCHPAD_POSITION).let { movingActors[1].setPosition(it.x, it.y) }
    }

    fun createInputListener() =
            object : InputListener() {
                override fun keyUp(event: InputEvent, keycode: Int): Boolean {
                    when (keycode) {
                        Input.Keys.W -> isJumping = false
                        Input.Keys.A -> isMovingLeft = false
                        Input.Keys.D -> isMovingRight = false
                        Input.Keys.UP -> isJumping = false
                        Input.Keys.LEFT -> isMovingLeft = false
                        Input.Keys.RIGHT -> isMovingRight = false
                        else -> return false
                    }
                    return true
                }

                override fun keyDown(event: InputEvent, keycode: Int): Boolean {
                    when (keycode) {
                        Input.Keys.W -> isJumping = true
                        Input.Keys.A -> isMovingLeft = true
                        Input.Keys.D -> isMovingRight = true
                        Input.Keys.UP -> isJumping = true
                        Input.Keys.LEFT -> isMovingLeft = true
                        Input.Keys.RIGHT -> isMovingRight = true
                        else -> return false
                    }
                    return true
                }
            }

    fun createChangeListener() =
            object : ChangeListener() {
                override fun changed(event: ChangeEvent, actor: Actor) {
                    when (actor.userObject) {
                        MovingInputType.SLIDER -> {
                            actor as Slider
                            isMovingLeft = actor.percent * 100f <= MOVING_INPUT_SLIDER_ACTIVATION_THRESHOLD_PERCENT
                            isMovingRight = actor.percent * 100f >= 100f - MOVING_INPUT_SLIDER_ACTIVATION_THRESHOLD_PERCENT
                        }

                        MovingInputType.TOUCHPAD -> {
                            actor as Touchpad
                            isMovingLeft = actor.knobPercentX < 0
                            isMovingRight = actor.knobPercentX > 0
                        }

                        else -> return
                    }
                    event.handle()
                }
            }

    override fun dispose() {
        resources.forEach { it.dispose(game.textureManager) }
    }
}

private fun setMyClickListener(button: Button) =
        button.apply {
            removeListener(clickListener)
            Button::class.java.getDeclaredField("clickListener").let {
                it.isAccessible = true
                // changing behaviour: button should be pressed if touchDragged() is called and mouse is not over the actor
                it.set(this, object : ClickListener() {
                    override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {}
                })
            }
            addListener(clickListener)
        }
