package io.github.advancerman.todd.asset

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.math.Vector2
import io.github.advancerman.todd.asset.font.FontSettings
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.json.SerializationType
import io.github.advancerman.todd.launcher.ToddGame

object AssetsParser {
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

    /**
     * @param path Font path relative to fonts/ directory
     * @param size Size in pixels
     * @param mono If true, font smoothing is disabled.
     * @param color Foreground color
     * @param borderWidth Border width in pixels, `0` to disable
     * @param borderColor Border color; only used if borderWidth `> 0`
     * @param borderStraight True for straight (mitered), false for rounded borders
     * @param characters The characters the font should contain.
     *                   "Missing character" is always included.
     * @param flip Whether to flip the font vertically
     */
    @SerializationType(BitmapFont::class)
    fun loadFont(
        game: ToddGame,
        path: String,
        size: Int = 16,
        mono: Boolean = false,
        color: Color = Color.BLACK,
        borderWidth: Float = 0f,
        borderColor: Color = Color.BLACK,
        borderStraight: Boolean = false,
        characters: String = FreeTypeFontGenerator.DEFAULT_CHARS,
        flip: Boolean = false,
    ): BitmapFont {
        val withMissingCharacters = if (characters.contains('\u0000')) {
            characters
        } else {
            characters + '\u0000'
        }
        return game.fontManager.load(FontSettings(
            path, size, mono, color, borderWidth, borderColor,
            borderStraight, withMissingCharacters, flip
        ))
    }
}
