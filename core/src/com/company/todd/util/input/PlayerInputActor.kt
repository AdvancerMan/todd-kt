package com.company.todd.util.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
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
                    Slider.SliderStyle(SpriteDrawable(resources[0]), SpriteDrawable(resources[1]))
            ).apply {
                width = MOVING_INPUT_SLIDER_WIDTH
                height = MOVING_INPUT_SLIDER_HEIGHT
                value = (maxValue + minValue) / 2
                val position = calculatePosition(MOVING_INPUT_SLIDER_POSITION)
                setPosition(position.x, position.y)
                userObject = MovingInputType.SLIDER
            },

            Touchpad(
                    0f,
                    Touchpad.TouchpadStyle(SpriteDrawable(resources[2]), SpriteDrawable(resources[3]))
            ).apply {
                width = MOVING_INPUT_TOUCHPAD_WIDTH
                height = MOVING_INPUT_TOUCHPAD_HEIGHT
                val position = calculatePosition(MOVING_INPUT_TOUCHPAD_POSITION)
                setPosition(position.x, position.y)
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

    private val jumpButton = Button(SpriteDrawable(resources[4]), SpriteDrawable(resources[5])).apply {
        val position = calculatePosition(JUMP_BUTTON_POSITION)
        setPosition(position.x, position.y)
        width = JUMP_BUTTON_WIDTH
        height = JUMP_BUTTON_HEIGHT
    }

    init {
        movingActors.forEach {
            addActor(it)
            it.isVisible = false
        }
        movingActors[inputActorIndex].isVisible = true
        addListener(createChangeListener())

        addActor(jumpButton)
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
