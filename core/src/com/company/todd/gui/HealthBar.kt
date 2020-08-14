package com.company.todd.gui

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.company.todd.launcher.ToddGame
import com.company.todd.asset.texture.DisposableByManager
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.TextureManager

class HealthBar(
        maxHealth: Float, stepSize: Float, animateDuration: Float,
        private val background: MyDrawable, private val healthDrawable: MyDrawable
) :
        ProgressBar(
                0f, maxHealth, stepSize, false,
                ProgressBarStyle().apply {
                    this.background = background
                    this.knobBefore = healthDrawable
                }
        ), DisposableByManager {
    init {
        value = maxHealth
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
