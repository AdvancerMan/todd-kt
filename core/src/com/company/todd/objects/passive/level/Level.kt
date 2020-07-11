package com.company.todd.objects.passive.level

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.passive.PassiveObjectInfo
import com.company.todd.screen.GameScreen

class Level(private val levelObjects: MutableList<PassiveObjectInfo> = mutableListOf()) {
    fun addObject(info: PassiveObjectInfo) {
        levelObjects.add(info)
    }

    fun create(game: ToddGame, screen: GameScreen) {
        levelObjects.forEach { it.create(game, screen) }
    }
}

fun levelOf(vararg levelObjects: PassiveObjectInfo) = Level(levelObjects.toMutableList())
