package com.company.todd.objects.passive

import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject

data class Level(val name: String, private val levelObjects: MutableList<(ToddGame) -> InGameObject> = mutableListOf()) {
    fun addObject(objectCreator: (ToddGame) -> InGameObject) {
        levelObjects.add(objectCreator)
    }

    fun create(game: ToddGame) =
            levelObjects.map { it(game) }
}
