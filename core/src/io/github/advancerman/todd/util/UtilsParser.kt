package io.github.advancerman.todd.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import io.github.advancerman.todd.json.SerializationType

object UtilsParser {
    /**
     * Color in rgba components. Valid range for parameters is `0..255`.
     */
    @SerializationType(Color::class)
    fun createColor(r: Int, g: Int, b: Int, a: Int = 255): Color {
        var values = listOf(r, g, b, a)
        if (values.any { it !in 0..255 }) {
            val newValues = values.map { it.coerceIn(0..255) }
            Gdx.app.error("Color", "Invalid value for color $values. Clamping to $newValues")
            values = newValues
        }
        val (rr, gg, bb, aa) = values
        // Color#toIntBits puts bits in abgr order, so we need to reverse our values to get rgba
        return Color(Color.toIntBits(aa, bb, gg, rr))
    }
}
