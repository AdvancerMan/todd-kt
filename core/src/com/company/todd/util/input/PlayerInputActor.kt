package com.company.todd.util.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.*

import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
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

    private val resources = listOf(
            game.textureManager.loadSprite("sliderBackground"),
            game.textureManager.loadSprite("sliderKnob"),
            game.textureManager.loadSprite("touchpadBackground"),
            game.textureManager.loadSprite("touchpadKnob")
    )

    private val actors = listOf(
            Slider(
                    0f, 100f, 1f, false,
                    Slider.SliderStyle(TextureRegionDrawable(resources[0]), TextureRegionDrawable(resources[1]))
            ).apply {
                width = MOVING_INPUT_SLIDER_WIDTH
                height = MOVING_INPUT_SLIDER_HEIGHT
                value = (maxValue + minValue) / 2
            },
            Touchpad(
                    0f,
                    Touchpad.TouchpadStyle(TextureRegionDrawable(resources[2]), TextureRegionDrawable(resources[3]))
            ).apply {
                width = MOVING_INPUT_TOUCHPAD_WIDTH
                height = MOVING_INPUT_TOUCHPAD_HEIGHT
            }
    )

    private var inputActorIndex = MOVING_INPUT_DEFAULT_ACTOR_INDEX
        set(value) {
            actors[field].isVisible = false
            actors[value].isVisible = true
            field = value
        }

    init {
        actors.forEach {
            addActor(it)
            it.isVisible = false
        }
        actors[inputActorIndex].isVisible = true
        addListener(createChangeListener())
    }

    fun setInputActor(type: MovingInputType) {
        inputActorIndex = type.i
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        setPosition(stage.camera.position.x - stage.camera.viewportWidth / 2 + 30f,
                stage.camera.position.y - stage.camera.viewportHeight / 2 + 30f)
        super.draw(batch, parentAlpha)
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
                    val inputActor = actors[inputActorIndex]
                    when (MovingInputType.values()[inputActorIndex]) {
                        MovingInputType.SLIDER -> {
                            inputActor as Slider
                            isMovingLeft = inputActor.percent * 100f <= MOVING_INPUT_SLIDER_ACTIVATION_THRESHOLD_PERCENT
                            isMovingRight = inputActor.percent * 100f >= 100f - MOVING_INPUT_SLIDER_ACTIVATION_THRESHOLD_PERCENT
                        }

                        MovingInputType.TOUCHPAD -> {
                            inputActor as Touchpad
                            isMovingLeft = inputActor.knobPercentX < 0
                            isMovingRight = inputActor.knobPercentX > 0
                        }
                    }
                }
            }

    override fun dispose() {
        resources.forEach { it.dispose(game.textureManager) }
    }
}
