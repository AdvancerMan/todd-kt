package io.github.advancerman.todd.asset.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.utils.Disposable
import io.github.advancerman.todd.asset.AssetManager
import io.github.advancerman.todd.util.FONTS_PATH

class FontManager : AssetManager<BitmapFont, FontSettings>(BitmapFont::class.java) {
    private val generatorManager =
        object : AssetManager<FreeTypeFontGeneratorWrapper, String>(BitmapFont::class.java) {
            override fun loadAsset(settings: String): FreeTypeFontGeneratorWrapper {
                return FreeTypeFontGeneratorWrapper(
                    FreeTypeFontGenerator(Gdx.files.internal("$FONTS_PATH$settings")),
                    this@FontManager
                )
            }

            override fun getDefaultAsset(): FreeTypeFontGeneratorWrapper {
                return FreeTypeFontGeneratorWrapper(null, this@FontManager)
            }
        }

    private val defaultFont by lazy {
        BitmapFont()
    }

    override fun loadAsset(settings: FontSettings): BitmapFont {
        return generatorManager.load(settings.path)
            .generateFont(settings.toFreeTypeFontParameter())
            .also { generatorManager.unload(settings.path) }
    }

    override fun getDefaultAsset() =
        defaultFont

    override fun dispose() {
        generatorManager.dispose()
        super.dispose()
    }

    class FreeTypeFontGeneratorWrapper(
        private val generator: FreeTypeFontGenerator?,
        private val manager: FontManager
    ) : Disposable {
        fun generateFont(parameter: FreeTypeFontGenerator.FreeTypeFontParameter): BitmapFont =
            generator?.generateFont(parameter) ?: manager.defaultFont

        override fun dispose() {
            generator?.dispose()
        }
    }
}
