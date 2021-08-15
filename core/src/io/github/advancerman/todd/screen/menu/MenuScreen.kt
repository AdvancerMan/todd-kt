package io.github.advancerman.todd.screen.menu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.asset.texture.withMinSize
import io.github.advancerman.todd.json.deserialization.construct
import io.github.advancerman.todd.json.deserialization.jsonSettings
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.screen.MyScreen

open class MenuScreen(game: ToddGame) : MyScreen(game) {
    private val settings = jsonSettings["menu"]
        ?: throw IllegalArgumentException("settings.json should contain menu parameter")

    private val font = settings["font"].construct<BitmapFont>(game)

    private val buttonUpDrawable =
        settings["button"]["up"].construct<ToddDrawable>(game).withMinSize()
    private val buttonDownDrawable =
        settings["button"]["down"].construct<ToddDrawable>(game).withMinSize()

    protected fun textButton(text: String) =
        TextButton(text, TextButton.TextButtonStyle(buttonUpDrawable, buttonDownDrawable, null, font))

    protected fun label(text: String) =
        Label(text, Label.LabelStyle(font, Color.BLACK))
}
