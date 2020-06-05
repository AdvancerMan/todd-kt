package com.company.todd.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import kotlin.math.abs

class RainbowScreen(private val maxValue: Float, private val step: Float): Screen {
    private val rgb = arrayOf(0f, maxValue, 0f)
    private var ind = 1

    private fun updateColor(delta: Float) {
        var realInd = abs(ind) - 1
        rgb[realInd] += (if (ind > 0) step else -step) * delta
        if (ind < 0 && rgb[realInd] < 0f || ind > 0 && rgb[realInd] > maxValue) {
            realInd = (realInd + 1) % 3 + 1
            ind = if (ind < 0) realInd else -realInd
        }
    }

    override fun render(delta: Float) {
        updateColor(delta)
        rgb.map { it / maxValue }.let {
            Gdx.gl.glClearColor(it[0], it[1], it[2], 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        }
    }

    override fun hide() {}
    override fun show() {}
    override fun pause() {}
    override fun resume() {}
    override fun resize(width: Int, height: Int) {}
    override fun dispose() {}
}
