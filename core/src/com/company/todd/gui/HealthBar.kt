package com.company.todd.gui

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.company.todd.launcher.ToddGame
import com.company.todd.asset.texture.DisposableByManager
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.TextureManager

// TODO to json
const val defaultHBStepSize = 0.1f
const val defaultHBAnimateDuration = 0.1f

class HealthBar(
        game: ToddGame, maxHealth: Float,
        stepSize: Float = defaultHBStepSize, animateDuration: Float = defaultHBAnimateDuration,
        private val background: MyDrawable = game.textureManager.loadDrawable("healthBarBackground"),
        private val healthDrawable: MyDrawable = game.textureManager.loadDrawable("healthBarHealth")
) :
        ProgressBar(
                0f, maxHealth, stepSize, false,
                ProgressBarStyle().apply {
                    this.background = background
                    this.knobBefore = healthDrawable
                }
        ), DisposableByManager {
    init {
        setAnimateDuration(animateDuration)
    }

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
