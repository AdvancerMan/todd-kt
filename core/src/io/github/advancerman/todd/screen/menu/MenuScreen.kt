package io.github.advancerman.todd.screen.menu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.asset.texture.withMinSize
import io.github.advancerman.todd.json.deserialization.jsonConstructors
import io.github.advancerman.todd.json.deserialization.jsonSettings
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.screen.MyScreen

open class MenuScreen(game: ToddGame) : MyScreen(game) {
    private val settings = jsonSettings["menu"]
        ?: throw IllegalArgumentException("settings.json should contain menu parameter")

    @Suppress("UNCHECKED_CAST")
    private val drawableFromJson = jsonConstructors[ToddDrawable::class]!![""]!!.constructor
            as (ToddGame, JsonValue) -> ToddDrawable

    // TODO menu font
    private val font = BitmapFont()

    private val buttonUpDrawable = drawableFromJson(game, settings["button"]!!["up"]!!)
        .withMinSize()
    private val buttonDownDrawable = drawableFromJson(game, settings["button"]!!["down"]!!)
        .withMinSize()

    protected fun textButton(text: String) =
        TextButton(text, TextButton.TextButtonStyle(buttonUpDrawable, buttonDownDrawable, null, font))

    protected fun label(text: String) =
        Label(text, Label.LabelStyle(font, Color.BLACK))
}
