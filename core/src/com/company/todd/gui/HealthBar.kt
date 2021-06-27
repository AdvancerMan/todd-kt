package com.company.todd.gui

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.company.todd.asset.texture.DisposableByManager
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.TextureManager
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.JsonUpdateSerializable

class HealthBar(
        maxHealth: Float, private val background: MyDrawable,
        private val healthDrawable: MyDrawable
) :
        ProgressBar(
                0f, maxHealth, STEP_SIZE, false,
                ProgressBarStyle().apply {
                    this.background = background
                    this.knobBefore = healthDrawable
                }
        ), DisposableByManager {
    init {
        value = maxHealth
        setAnimateDuration(ANIMATE_DURATION)
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

    @JsonUpdateSerializable("maxHealth")
    override fun getMaxValue(): Float {
        return super.getMaxValue()
    }

    @JsonFullSerializable("backgroundDrawableName")
    private fun getBackgroundDrawableName() = background.drawableName

    @JsonFullSerializable("healthDrawableName")
    private fun getHealthDrawableName() = background.drawableName

    companion object {
        const val ANIMATE_DURATION = 0.1f
        const val STEP_SIZE = 0.1f
    }
}
