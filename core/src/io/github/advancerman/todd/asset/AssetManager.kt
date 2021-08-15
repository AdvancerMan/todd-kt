package io.github.advancerman.todd.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Queue
import io.github.advancerman.todd.util.SAVING_ASSET_DELAY

abstract class AssetManager<T: Disposable, S : Any?>(logTagClass: Class<*>): Disposable {
    private val assets = mutableMapOf<S, Asset<T>>()
    private val unloadingQueue = Queue<UnloadRequest<S>>()
    private var secondsFromCreation = 0f

    private val logTag = "AssetManager<${logTagClass.simpleName}>"

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
        while (unloadingQueue.notEmpty() && unloadingQueue.first().unloadMoment < secondsFromCreation) {
            val (settings, _, amount) = unloadingQueue.removeFirst()
            val asset = assets[settings]
            if (asset == null) {
                error("Trying to unload non-existing asset: $settings")
            } else {
                asset.refCount -= amount
                if (asset.refCount < 0) {
                    error("Trying to unload asset too many times " +
                            "(from ${asset.refCount + amount} subtracting $amount): $settings")
                }
                if (asset.refCount <= 0) {
                    disposeAsset(asset, settings)
                }
            }
        }
    }

    private fun disposeAsset(asset: Asset<T>, settings: S) {
        debug("Disposing asset: $settings")
        asset.dispose()
        assets.remove(settings)
    }

    fun load(settings: S): T {
        val asset = assets[settings] ?: Asset(loadAsset(settings)).also {
            assets[settings] = it
            debug("Loading asset: $settings")
        }
        asset.refCount++
        return asset.asset
    }

    protected abstract fun loadAsset(settings: S): T

    protected fun unload(settings: S, amount: Int) {
        unloadingQueue.addLast(
            UnloadRequest(settings, secondsFromCreation + SAVING_ASSET_DELAY, amount)
        )
    }

    fun unload(settings: S) {
        unload(settings, 1)
    }

    override fun dispose() =
            assets.forEach { it.value.asset.dispose() }

    private data class Asset<T: Disposable>(val asset: T, var refCount: Int = 0): Disposable {
        override fun dispose() =
                asset.dispose()
    }

    private data class UnloadRequest<S : Any?>(
        val settings: S,
        val unloadMoment: Float,
        val unloadAmount: Int
    )
}
