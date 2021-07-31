package com.company.todd.asset.texture

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.json.JsonDefaults
import com.company.todd.json.ManualJsonConstructor
import com.company.todd.json.SerializationType
import com.company.todd.launcher.ToddGame

object DrawableParser {
    /**
     * Texture region(s) wrapper
     *
     * @param name Texture info name from which drawable should be loaded
     * @param zIndex Drawable zIndex, described by drawable owner
     * @param size Drawable size, described by drawable owner
     * @param offset Drawable offset, described by drawable owner
     */
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
