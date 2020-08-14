package com.company.todd.util

import com.company.todd.input.MovingInputType

// fps ^ (-1)
var SPF = 1f / 60

const val SCREEN_WIDTH = 800
const val SCREEN_HEIGHT = 450

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

const val LEVELS_PATH = "levels/"
const val TEXTURES_PATH = "pics/"
const val PROTOTYPES_PATH = "prototypes/"

const val DEFAULT_DENSITY = 1f
const val DEFAULT_FRICTION = 0f
const val DEFAULT_RESTITUTION = 0f
const val DEFAULT_LINEAR_DAMPING = 0.5f

const val BOTTOM_SENSOR_OFFSET = 1f
const val BOTTOM_SENSOR_CUTTING_COEFFICIENT = 0.9f

const val SMOOTH_RECT_BOTTOM_ANGLE = 61f
const val SMOOTH_RECT_SMOOTH_COEFFICIENT = 0.1f
const val SMOOTH_RECT_MAX_SMOOTHNESS = 3f

const val SAVING_ASSET_DELAY = 5f
