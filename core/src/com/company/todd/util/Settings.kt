package com.company.todd.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.company.todd.util.input.MovingInputType

// fps ^ (-1)
var SPF = 1f / 60

const val SCREEN_WIDTH = 800
const val SCREEN_HEIGHT = 640

const val MOVING_INPUT_SLIDER_WIDTH = 140f
const val MOVING_INPUT_SLIDER_HEIGHT = 20f
val MOVING_INPUT_SLIDER_POSITION = 30f to 30f
const val MOVING_INPUT_SLIDER_ACTIVATION_THRESHOLD_PERCENT = 20f
const val MOVING_INPUT_TOUCHPAD_WIDTH = 100f
const val MOVING_INPUT_TOUCHPAD_HEIGHT = 100f
val MOVING_INPUT_TOUCHPAD_POSITION = MOVING_INPUT_SLIDER_POSITION
val MOVING_INPUT_DEFAULT_ACTOR_INDEX = MovingInputType.TOUCHPAD.i
val JUMP_BUTTON_POSITION = -90f to 30f
const val JUMP_BUTTON_WIDTH = 50f
const val JUMP_BUTTON_HEIGHT = JUMP_BUTTON_WIDTH
