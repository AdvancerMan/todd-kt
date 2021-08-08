package io.github.advancerman.todd.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Queue
import io.github.advancerman.todd.util.SAVING_ASSET_DELAY

abstract class AssetManager<T: Disposable>(clazz: Class<T>): Disposable {
    private val assets = mutableMapOf<String, Asset<T>>()
    private val unloadingQueue = Queue<Pair<Pair<String, Float>, Int>>()
    private var secondsFromCreation = 0f

    private val logTag = "AssetManager<${clazz.simpleName}>"

    protected fun error(message: String) =
            Gdx.app.error(logTag, message)

    protected fun error(message: String, exception: Throwable) =
            Gdx.app.error(logTag, message, exception)

    protected fun debug(message: String) =
            Gdx.app.debug(logTag, message)

    protected fun log(message: String) =
            Gdx.app.log(logTag, message)

    fun update(delta: Float) {
        secondsFromCreation += delta
        while (unloadingQueue.notEmpty() && unloadingQueue.first().first.second < secondsFromCreation) {
            val fileName = unloadingQueue.first().first.first
            val count = unloadingQueue.removeFirst().second
            val asset = assets[fileName]
            if (asset == null) {
                error("Trying to unload non-existing asset: $fileName")
            } else {
                asset.refCount -= count
                if (asset.refCount < 0) {
                    error("Trying to unload asset too many times " +
                            "(from ${asset.refCount + count} subtracting $count): $fileName")
                }
                if (asset.refCount <= 0) {
                    disposeAsset(asset, fileName)
                }
            }
        }
    }

    private fun disposeAsset(asset: Asset<T>, fileName: String) {
        debug("Disposing asset: $fileName")
        asset.dispose()
        assets.remove(fileName)
    }

    fun load(fileName: String): T {
        val asset = assets[fileName] ?: Asset(loadAsset(fileName)).also {
            assets[fileName] = it
            debug("Loading asset: $fileName")
        }
        asset.refCount++
        return asset.asset
    }

    abstract fun loadAsset(fileName: String): T

    protected fun unload(fileName: String, count: Int) {
        unloadingQueue.addLast(fileName to secondsFromCreation + SAVING_ASSET_DELAY to count)
    }

    fun unload(fileName: String) {
        unload(fileName, 1)
    }

    override fun dispose() =
            assets.forEach { it.value.asset.dispose() }

    private data class Asset<T: Disposable>(val asset: T, var refCount: Int = 0): Disposable {
        override fun dispose() =
                asset.dispose()
    }
}
