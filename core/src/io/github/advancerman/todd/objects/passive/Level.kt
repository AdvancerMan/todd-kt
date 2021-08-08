package io.github.advancerman.todd.objects.passive

import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.objects.base.InGameObject

data class Level(val name: String, private val levelObjects: MutableList<(ToddGame) -> InGameObject> = mutableListOf()) {
    fun addObject(objectCreator: (ToddGame) -> InGameObject) {
        levelObjects.add(objectCreator)
    }

    fun create(game: ToddGame) =
            levelObjects.map { it(game) }
}
