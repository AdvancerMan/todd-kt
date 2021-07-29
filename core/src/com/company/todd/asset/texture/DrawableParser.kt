package com.company.todd.asset.texture

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.json.JsonDefaults
import com.company.todd.json.ManualJsonConstructor
import com.company.todd.json.SerializationType
import com.company.todd.launcher.ToddGame

object DrawableParser {
    @SerializationType(ToddDrawable::class)
    private fun loadDrawable(game: ToddGame, name: String, zIndex: Int, size: Vector2, offset: Vector2) =
        game.textureManager.loadDrawable(name).apply {
            drawableName = name
            myZIndex = zIndex
            this.size.set(size)
            this.offset.set(offset)
        }

    @ManualJsonConstructor("loadDrawable")
    private fun drawableDefaults(
        @Suppress("UNUSED_PARAMETER") json: JsonValue,
        parsed: MutableMap<String, Pair<Any?, Boolean>>
    ) {
        JsonDefaults.setDefault("zIndex", 0, parsed)
        JsonDefaults.setDefault("size", Vector2(), parsed)
        JsonDefaults.setDefault("offset", Vector2(), parsed)
    }
}
