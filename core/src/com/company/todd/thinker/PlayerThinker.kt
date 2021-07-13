package com.company.todd.thinker

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pools
import com.company.todd.gui.ButtonInputActor
import com.company.todd.gui.ScreenInputActor
import com.company.todd.json.deserialization.float
import com.company.todd.json.deserialization.get
import com.company.todd.json.deserialization.jsonSettings
import com.company.todd.json.deserialization.string
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.creature.Creature
import com.company.todd.screen.game.GameScreen

enum class MovingInputType(val jsonName: String) {
    SLIDER("slider"), TOUCHPAD("touchpad"), MOVING_BUTTONS("moveButtons")
}

class PlayerThinker(val game: ToddGame) : Group(), Thinker, Disposable {
    private var isMovingLeft = false
    private var isMovingRight = false
    private var isJumping = false
    private var isAttacking = false

    private val settings = jsonSettings["input"]

    private val resources = settings
        .filter { it.isObject }
        .associate { json ->
            json.name to json
                .filter { it.name.endsWith("drawablename", true) }
                .associate { nameJson ->
                    nameJson.name.substring(0, nameJson.name.length - "drawablename".length) to
                            game.textureManager.loadDrawable(nameJson.asString())
                }
        }
        .filter { it.value.isNotEmpty() }

    private val movingActors = listOf(
        "slider" to object : ScreenInputActor<Slider>(
            Slider(
                0f, 100f, 1f, false,
                Slider.SliderStyle(
                    resources["slider"]!!["background"]!!,
                    resources["slider"]!!["knob"]!!
                )
            ), settings["slider"]
        ) {
            private val activationFraction = settings["slider"]["activationFraction", float]

            override fun reset() {
                actor.value = (actor.maxValue + actor.minValue) / 2
            }

            override fun changed(event: ChangeListener.ChangeEvent) {
                isMovingLeft = actor.percent <= activationFraction
                isMovingRight = actor.percent >= 1 - activationFraction
            }
        },

        "touchpad" to object : ScreenInputActor<Touchpad>(
            Touchpad(
                0f,
                Touchpad.TouchpadStyle(
                    resources["touchpad"]!!["background"]!!,
                    resources["touchpad"]!!["knob"]!!
                )
            ), settings["touchpad"]
        ) {
            override fun changed(event: ChangeListener.ChangeEvent) {
                isMovingLeft = actor.knobPercentX < 0
                isMovingRight = actor.knobPercentX > 0
            }
        },

        "moveButtons" to ButtonInputActor(settings, resources, "moveButtonLeft") {
            isMovingLeft = it
        },

        "moveButtons" to ButtonInputActor(settings, resources, "moveButtonRight") {
            isMovingRight = it
        }
    )

    private val actors = listOf(
        *movingActors.map { it.second }.toTypedArray(),
        ButtonInputActor(settings, resources, "jumpButton") { isJumping = it },
        ButtonInputActor(settings, resources, "attackButton") { isAttacking = it }
    )

    init {
        addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor) {
                (actor.userObject as ScreenInputActor<*>).changed(event)
            }
        })

        val defaultActor = settings["defaultMovingActor", string]
        movingActors.forEach {
            it.second.actor.isVisible = it.first == defaultActor
        }

        actors.forEach {
            this.addActor(it.actor)
            it.reset()
            it.updatePosition(width, height)
            if (it.actor is Button) {
                it.actor.setMyClickListener()
            }
        }
    }

    override fun think(delta: Float, operatedObject: Creature, screen: GameScreen) {
        if (isMovingLeft) {
            operatedObject.isDirectedToRight = false
            operatedObject.run(false)
        }
        if (isMovingRight) {
            operatedObject.isDirectedToRight = true
            operatedObject.run(true)
        }
        if (isJumping) {
            operatedObject.jump()
        }
        if (isAttacking) {
            operatedObject.attack()
        }
    }

    fun setMovingActor(type: MovingInputType) {
        isMovingLeft = false
        isMovingRight = false
        isJumping = false
        isAttacking = false

        movingActors.forEach {
            it.second.reset()
            it.second.actor.isVisible = it.first == type.jsonName
        }
    }

    fun resize(width: Float, height: Float) {
        setSize(width, height)
        actors.forEach { it.updatePosition(width, height) }
    }

    fun createInputListener() =
            object : InputListener() {
                override fun keyUp(event: InputEvent, keycode: Int): Boolean {
                    when (keycode) {
                        Input.Keys.W -> isJumping = false
                        Input.Keys.A -> isMovingLeft = false
                        Input.Keys.D -> isMovingRight = false
                        Input.Keys.X -> isAttacking = false
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
                        Input.Keys.X -> isAttacking = true
                        Input.Keys.UP -> isJumping = true
                        Input.Keys.LEFT -> isMovingLeft = true
                        Input.Keys.RIGHT -> isMovingRight = true
                        else -> return false
                    }
                    return true
                }
            }

    override fun dispose() {
        resources.values.forEach { actor ->
            actor.values.forEach { it.dispose(game.textureManager) }
        }
    }
}

private fun Button.setMyClickListener() {
    removeListener(clickListener)
    Button::class.java.getDeclaredField("clickListener").let {
        it.isAccessible = true
        // changing behaviour: button should be pressed if touchDragged() is called and mouse is not over the actor
        it.set(this, object : ClickListener() {
            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {}

            private inline fun <T> withPressedEvent(action: () -> T): T {
                val pressed = isPressed
                val result = action()
                if (pressed != isPressed) {
                    val changeEvent = Pools.obtain(ChangeListener.ChangeEvent::class.java)
                    fire(changeEvent)
                    Pools.free(changeEvent)
                }
                return result
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                withPressedEvent {
                    super.touchUp(event, x, y, pointer, button)
                }
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                return withPressedEvent {
                    super.touchDown(event, x, y, pointer, button)
                }
            }
        })
    }
    addListener(clickListener)
}
