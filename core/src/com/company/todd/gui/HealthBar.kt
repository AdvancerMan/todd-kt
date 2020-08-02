package com.company.todd.gui

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.company.todd.launcher.ToddGame
import com.company.todd.util.asset.texture.DisposableByManager
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.asset.texture.TextureManager

class HealthBar(game: ToddGame, maxHealth: Float, stepSize: Float = 0.1f,
                private val background: MyDrawable = game.textureManager.loadDrawable("healthBarBackground"),
                private val healthDrawable: MyDrawable = game.textureManager.loadDrawable("healthBarHealth")) :
        ProgressBar(
                0f, maxHealth, stepSize, false,
                ProgressBarStyle().apply {
                    this.background = background
                    this.knobBefore = healthDrawable
                }
        ), DisposableByManager {
    override fun act(delta: Float) {
        super.act(delta)
        background.update(delta)
        healthDrawable.update(delta)
    }

    override fun dispose(manager: TextureManager) {
        background.dispose(manager)
        healthDrawable.dispose(manager)
    }
}
