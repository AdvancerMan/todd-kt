package io.github.advancerman.todd.util

import com.badlogic.gdx.graphics.Color
import io.github.advancerman.todd.json.SerializationType

object UtilsParser {
    /**
     * Color in rgba components. Valid range for parameters is [0, 1].
     */
    @SerializationType(Color::class)
    fun createColor(r: Float, g: Float, b: Float, a: Float = 1f): Color {
        return Color(r, g, b, a)
    }
}
