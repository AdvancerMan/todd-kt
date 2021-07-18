package com.company.todd.objects.base

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.json.ManuallyJsonSerializable
import com.company.todd.json.deserialization.updateFromJson
import com.company.todd.json.serialization.toJsonFull
import com.company.todd.json.serialization.toJsonSave
import com.company.todd.json.serialization.toJsonUpdates

class DrawableActor : Actor(), ManuallyJsonSerializable {
    var flipX: Boolean = false
    var flipY: Boolean = false
    var drawable: MyDrawable? = null

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
