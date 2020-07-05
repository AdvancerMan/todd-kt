package com.company.todd.util.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Queue

const val savingAssetDelay = 5f

abstract class AssetManager<T: Disposable>(clazz: Class<T>): Disposable {
    private val assets = mutableMapOf<String, Asset<T>>()
    private val unloadingQueue = Queue<Pair<String, Float>>()
    private var secondsFromCreation = 0f

    private val logTag = "AssetManager " + clazz.simpleName

    protected fun error(message: String) =
            Gdx.app.error(logTag, message)

    protected fun debug(message: String) =
            Gdx.app.debug(logTag, message)

    protected fun log(message: String) =
            Gdx.app.log(logTag, message)

    fun update(delta: Float) {
        secondsFromCreation += delta
        while (unloadingQueue.notEmpty() && unloadingQueue.first().second < secondsFromCreation) {
            val fileName = unloadingQueue.removeFirst().first
            val asset = assets[fileName]
            if (asset == null) {
                error("Trying to unload non-existing asset: $fileName")
            } else if (--asset.refCount == 0) {
                disposeAsset(asset, fileName)
            }
        }
    }

    private fun disposeAsset(asset: Asset<T>, fileName: String) {
        debug("Disposing asset: $fileName")
        asset.dispose()
        assets.remove(fileName)
    }

    fun load(fileName: String): T {
        var asset = assets[fileName]
        if (asset == null) {
            debug("Loading asset: $fileName")
            asset = Asset(loadAsset(fileName))
        }
        asset.refCount++
        return asset.asset
    }

    abstract fun loadAsset(fileName: String): T

    fun unload(fileName: String) {
        unloadingQueue.addLast(fileName to secondsFromCreation + savingAssetDelay)
    }

    override fun dispose() =
            assets.forEach { it.value.asset.dispose() }

    private data class Asset<T: Disposable>(val asset: T, var refCount: Int = 0): Disposable {
        override fun dispose() =
                asset.dispose()
    }
}
