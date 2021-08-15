package io.github.advancerman.todd.asset.font

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator

data class FontSettings(
    val path: String,
    val size: Int,
    val mono: Boolean,
    val color: Color,
    val borderWidth: Float,
    val borderColor: Color,
    val borderStraight: Boolean,
    val characters: String,
    val flip: Boolean,
) {
    fun toFreetypeFontParameter(): FreeTypeFontGenerator.FreeTypeFontParameter {
        return FreeTypeFontGenerator.FreeTypeFontParameter().also {
            it.size = size
            it.mono = mono
            it.color = color
            it.borderWidth = borderWidth
            it.borderColor = borderColor
            it.borderStraight = borderStraight
            it.characters = characters
            it.flip = flip
        }
    }
}
