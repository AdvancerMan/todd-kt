package com.company.todd.objects.passive.level

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.passive.PassiveObject

data class Level(val name: String, private val levelObjects: MutableList<(ToddGame) -> PassiveObject> = mutableListOf()) {
    fun addObject(objectCreator: (ToddGame) -> PassiveObject) {
        levelObjects.add(objectCreator)
    }

    fun create(game: ToddGame) =
            levelObjects.map { it(game) }
}
