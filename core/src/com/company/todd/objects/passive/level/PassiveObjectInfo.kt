package com.company.todd.objects.passive.level

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.passive.PassiveObject
import com.company.todd.objects.passive.interactive.Jumper
import com.company.todd.objects.passive.interactive.Portal
import com.company.todd.objects.passive.interactive.Trampoline
import com.company.todd.objects.passive.interactive.Travolator
import com.company.todd.objects.passive.platform.CloudyPlatform
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.objects.passive.platform.SolidPolygonPlatform
import com.company.todd.objects.passive.platform.SolidRectanglePlatform
import com.company.todd.util.asset.texture.MyDrawable

private val FLOAT = { json: JsonValue -> json.asFloat() }
private val VECTOR = { json: JsonValue -> json.asFloatArray().let { Vector2(it[0], it[1]) } }
private val RECTANGLE = { json: JsonValue -> json.asFloatArray().let { Rectangle(it[0], it[1], it[2], it[3]) } }
private val VECTOR_ARRAY = { json: JsonValue -> json.map(VECTOR).toTypedArray() }

private class PassiveObjectConstructor(val constructor: (ToddGame, MyDrawable, JsonValue) -> PassiveObject)

private val SOLID_RECTANGLE_PRIMARY = PassiveObjectConstructor { game, drawable, json ->
    SolidRectanglePlatform(game, drawable, RECTANGLE(json[0]))
}

private val SOLID_POLYGON_PRIMARY = PassiveObjectConstructor { game, drawable, json ->
    SolidPolygonPlatform(game, drawable, VECTOR_ARRAY(json[0]), VECTOR(json[1]))
}

private val HALF_COLLIDED_PRIMARY = PassiveObjectConstructor { game, drawable, json ->
    HalfCollidedPlatform(game, drawable, RECTANGLE(json[0]))
}

private val CLOUDY_PRIMARY = PassiveObjectConstructor { game, drawable, json ->
    CloudyPlatform(game, drawable, RECTANGLE(json[0]), FLOAT(json[1]), FLOAT(json[2]))
}

private val JUMPER_PRIMARY = PassiveObjectConstructor { game, drawable, json ->
    Jumper(game, drawable, RECTANGLE(json[0]), FLOAT(json[1]))
}

private val PORTAL_PRIMARY = PassiveObjectConstructor { game, drawable, json ->
    Portal(game, drawable, VECTOR(json[0]), FLOAT(json[1]), VECTOR(json[2]), FLOAT(json[3]))
}

private val TRAMPOLINE_PRIMARY = PassiveObjectConstructor { game, drawable, json ->
    Trampoline(game, drawable, RECTANGLE(json[0]))
}

private val TRAVOLATOR_PRIMARY = PassiveObjectConstructor { game, drawable, json ->
    Travolator(game, drawable, RECTANGLE(json[0]), FLOAT(json[1]))
}

enum class PassiveObjectInfo(val constructor: (ToddGame, MyDrawable, JsonValue) -> PassiveObject,
                             val drawableName: String) {
    ARENA_SOLID_RECTANGLE(SOLID_RECTANGLE_PRIMARY, "arenaSolid"),
    ARENA_LEFT_TRIBUNE(SOLID_POLYGON_PRIMARY, "arenaLeftTribune"),
    ARENA_RIGHT_TRIBUNE(SOLID_POLYGON_PRIMARY, "arenaRightTribune"),
    ARENA_HALF_COLLIDED(HALF_COLLIDED_PRIMARY, "arenaHalfCollided"),
    ARENA_CLOUDY(CLOUDY_PRIMARY, "arenaCloudy"),
    ARENA_JUMPER(JUMPER_PRIMARY, "arenaJumper"),
    ARENA_PORTAL(PORTAL_PRIMARY, "arenaPortal"),
    ARENA_TRAMPOLINE(TRAMPOLINE_PRIMARY, "arenaTrampoline"),
    ARENA_TRAVOLATOR(TRAVOLATOR_PRIMARY, "arenaTravolator");

    constructor(constructor: PassiveObjectConstructor, drawableName: String) :
            this(constructor.constructor, drawableName)
}
