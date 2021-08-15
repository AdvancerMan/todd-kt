package io.github.advancerman.todd.util

// fps ^ (-1)
var SPF = 1f / 60

const val SCREEN_WIDTH = 800
const val SCREEN_HEIGHT = 450

const val LEVELS_PATH = "levels/"
const val TEXTURES_PATH = "pics/"
const val PROTOTYPES_PATH = "prototypes/"
const val FONTS_PATH = "fonts/"
const val SETTINGS_PATH = "settings.json"

const val DEFAULT_DENSITY = 1f
const val DEFAULT_FRICTION = 0f
const val DEFAULT_RESTITUTION = 0f
const val DEFAULT_LINEAR_DAMPING = 0.5f

const val BOTTOM_SENSOR_OFFSET = 1f
const val BOTTOM_SENSOR_CUTTING_COEFFICIENT = 0.9f

const val BOTTOM_GROUND_ANGLE = 61f
const val SMOOTH_RECT_BOTTOM_ANGLE = BOTTOM_GROUND_ANGLE - 1
const val SMOOTH_RECT_SMOOTH_COEFFICIENT = 0.1f
const val SMOOTH_RECT_MAX_SMOOTHNESS = 3f

const val SAVING_ASSET_DELAY = 5f

const val JUMP_COOLDOWN = 1 / 30f
const val Y_VEL_JUMP_THRESHOLD = 1f
const val HEALTH_BAR_OFFSET = 3f
const val DAMAGE_TINT_TIME = 0.4f

const val HALF_COL_GROUND_VEL_SCL = 2f
