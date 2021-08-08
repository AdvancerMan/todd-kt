package io.github.advancerman.todd.objects.base

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.JsonValue
import io.github.advancerman.todd.asset.texture.ToddDrawable
import io.github.advancerman.todd.asset.texture.WithZIndex
import io.github.advancerman.todd.json.ManuallyJsonSerializable
import io.github.advancerman.todd.json.deserialization.updateFromJson
import io.github.advancerman.todd.json.serialization.toJsonFull
import io.github.advancerman.todd.json.serialization.toJsonSave
import io.github.advancerman.todd.json.serialization.toJsonUpdates

class DrawableActor : Actor(), ManuallyJsonSerializable, WithZIndex {
    var flipX: Boolean = false
    var flipY: Boolean = false
    var drawable: ToddDrawable? = null

    override val myZIndex: Int
        get() = drawable!!.myZIndex

    override fun draw(batch: Batch, parentAlpha: Float) {
        val batchColor = batch.color.cpy()
        batch.color = batch.color.mul(color).apply { a *= parentAlpha }
        drawable?.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation, flipX, flipY)
        batch.color = batchColor
    }

    override fun serializeUpdates(json: JsonValue) {
        drawable?.toJsonUpdates()?.forEach { json.addChild(it) }
    }

    override fun deserializeUpdates(json: JsonValue) {
        drawable?.updateFromJson(json)
    }

    override fun serializeFull(json: JsonValue) {
        json.removeAll { true }
        drawable?.toJsonFull()?.forEach { json.addChild(it) }
    }

    override fun serializeSave(json: JsonValue) {
        json.removeAll { true }
        drawable?.toJsonSave()?.forEach { json.addChild(it) }
    }
}
