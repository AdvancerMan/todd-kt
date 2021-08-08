package io.github.advancerman.todd.gui

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.json.deserialization.get
import io.github.advancerman.todd.json.deserialization.vector

open class ScreenInputActor<A : Actor>(val actor: A, json: JsonValue) {
    private val position: Vector2 = json["position", vector]

    init {
        val size = json["size", vector]
        actor.setSize(size.x, size.y)

        @Suppress("LeakingThis")
        actor.userObject = this
    }

    fun updatePosition(screenWidth: Float, screenHeight: Float) {
        actor.setPosition((screenWidth + position.x) % screenWidth, (screenHeight + position.y) % screenHeight)
    }

    open fun reset() {
        // no operations
    }

    open fun changed(event: ChangeListener.ChangeEvent) {
        // no operations
    }
}
