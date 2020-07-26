package com.company.todd.objects.passive.level

import com.company.todd.objects.passive.PassiveObject
import com.company.todd.objects.passive.interactive.Jumper
import com.company.todd.objects.passive.interactive.Portal
import com.company.todd.objects.passive.interactive.Trampoline
import com.company.todd.objects.passive.interactive.Travolator
import com.company.todd.objects.passive.platform.CloudyPlatform
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.objects.passive.platform.SolidRectanglePlatform
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility

enum class PassiveObjectInfo(val constructor: KFunction<PassiveObject>, val drawableName: String) {
    ARENA_CLOUDY(CloudyPlatform::class, "arenaCloudy"),
    ARENA_HALF_COLLIDED(HalfCollidedPlatform::class, "arenaHalfCollided"),
    ARENA_SOLID(SolidRectanglePlatform::class, "arenaSolid"),
    ARENA_JUMPER(Jumper::class, "arenaJumper"),
    ARENA_PORTAL(Portal::class, "arenaPortal"),
    ARENA_TRAMPOLINE(Trampoline::class, "arenaTrampoline"),
    ARENA_TRAVOLATOR(Travolator::class, "arenaTravolator");

    constructor(clazz: KClass<out PassiveObject>, drawableName: String) :
            this(getPassiveConstructor(clazz), drawableName)
}

private fun <T : PassiveObject> getPassiveConstructor(clazz: KClass<T>): KFunction<T> {
    require(clazz.constructors.count { it.visibility == KVisibility.PUBLIC } == 1) {
        "To use ${clazz.simpleName} in level should have only 1 public constructor"
    }
    return clazz.constructors.first { it.visibility == KVisibility.PUBLIC }
}
