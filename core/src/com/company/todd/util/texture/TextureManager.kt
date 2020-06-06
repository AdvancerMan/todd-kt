package com.company.todd.util.texture

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException

const val savingTexturesDelay = 5f

class TextureManager: Disposable {
    private val textures = mutableMapOf<String, Pair<Texture, Int>>()
    private val unusedTextures = mutableMapOf<String, Pair<Texture, Float>>()
    private val blackSquare = Texture(Pixmap(10, 10, Pixmap.Format.RGBA8888).apply { setColor(Color.BLACK) })

    fun update(delta: Float) {
        val it = unusedTextures.iterator()
        while (it.hasNext()) {
            val entry = it.next().apply {
                setValue(value.first to value.second - delta)
            }

            if (entry.value.second < 0) {
                entry.value.first.dispose()
                it.remove()
            }
        }
    }

    // TODO replace fileName with enums
    // TODO loadAnimation() loadAnimationPack()
    fun loadAnimation(fileName: String): Animation<TextureRegion> {
        TODO()
    }

    fun loadTextureRegion(fileName: String, x: Int, y: Int, w: Int, h: Int) =
            TextureRegion(loadTexture(fileName), x, y, w, h)

    fun loadTexture(fileName: String) = loadTexture(fileName, 1)
    fun disposeTexture(fileName: String) = disposeTexture(fileName, 1)

    private fun loadTexture(fileName: String, usages: Int): Texture {
        val pair = textures[fileName]
                ?: (unusedTextures.remove(fileName)?.first ?: loadTextureFromDisk(fileName)) to 0
        textures[fileName] = pair.first to pair.second + usages
        return pair.first
    }

    private fun loadTextureFromDisk(fileName: String) =
            try {
                Texture(fileName)
            } catch (e: GdxRuntimeException) {
                e.printStackTrace()
                blackSquare
            }

    private fun disposeTexture(fileName: String, usages: Int) {
        val txt = textures[fileName] ?: error("Trying to dispose unused texture")

        when {
            txt.second > usages -> textures[fileName] = txt.first to txt.second - usages
            txt.second == usages -> {
                textures.remove(fileName)
                unusedTextures[fileName] = txt.first to savingTexturesDelay
            }
            else -> error("Trying to dispose texture with 0 usages")
        }
    }

    override fun dispose() {
        textures.forEach { it.value.first.dispose() }
        unusedTextures.forEach { it.value.first.dispose() }
        blackSquare.dispose()
    }
}
