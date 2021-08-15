package io.github.advancerman.todd.asset.font

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import io.github.advancerman.todd.asset.AssetManager
import io.github.advancerman.todd.util.FONTS_PATH

class FontManager : AssetManager<BitmapFont, FontSettings>(BitmapFont::class.java) {
    private val generatorManager =
        object : AssetManager<FreeTypeFontGenerator, String>(BitmapFont::class.java) {
            override fun loadAsset(settings: String): FreeTypeFontGenerator {
                return FreeTypeFontGenerator(Gdx.files.internal("$FONTS_PATH$settings"))
            }
        }

    override fun loadAsset(settings: FontSettings): BitmapFont {
        return generatorManager.load(settings.path)
            .generateFont(settings.toFreetypeFontParameter())
            .also { generatorManager.unload(settings.path) }
    }

    override fun dispose() {
        generatorManager.dispose()
        super.dispose()
    }
}
