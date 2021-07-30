package com.company.todd.asset.texture

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.json.JsonDefaults
import com.company.todd.json.ManualJsonConstructor
import com.company.todd.json.SerializationType
import com.company.todd.launcher.ToddGame

object DrawableParser {
    @SerializationType(ToddDrawable::class)
    private fun loadDrawable(
        game: ToddGame,
        name: String,
        zIndex: Int = 0,
        size: Vector2 = Vector2(0f, 0f),
        offset: Vector2 = Vector2(0f, 0f)
    ) =
        game.textureManager.loadDrawable(name).apply {
            drawableName = name
            myZIndex = zIndex
            this.size.set(size)
            this.offset.set(offset)
        }
}
