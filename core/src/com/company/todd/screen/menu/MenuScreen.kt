package com.company.todd.screen.menu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.company.todd.launcher.ToddGame
import com.company.todd.screen.MyScreen

open class MenuScreen(game: ToddGame) : MyScreen(game) {
    // TODO menu font
    private val font = BitmapFont()

    // not pressed, button value = false
    private val buttonUpDrawable = game.textureManager.loadDrawable("menuButtonUp")
    // not pressed, button value = true
    private val buttonCheckedDrawable = game.textureManager.loadDrawable("menuButtonChecked")
    // pressed
    private val buttonDownDrawable = game.textureManager.loadDrawable("menuButtonDown")

    protected fun textButton(text: String) =
        TextButton(text, TextButton.TextButtonStyle(buttonUpDrawable, buttonDownDrawable, buttonCheckedDrawable, font))

    protected fun label(text: String) =
        Label(text, Label.LabelStyle(font, Color.BLACK))
}
