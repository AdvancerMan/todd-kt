package com.company.todd.gui

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.asset.texture.*
import com.company.todd.json.*
import com.company.todd.json.deserialization.float
import com.company.todd.json.deserialization.get
import com.company.todd.json.serialization.toJsonValue

// TODO make global z index
@SerializationType("healthBar")
class HealthBar(
    maxHealth: Float,
    @JsonFullSerializable private val backgroundDrawable: MyDrawable,
    @JsonFullSerializable private val healthDrawable: MyDrawable,
    @JsonFullSerializable("zIndex") override val myZIndex: Int
) :
        ProgressBar(
                0f, maxHealth, STEP_SIZE, false,
                ProgressBarStyle().apply {
                    this.background = backgroundDrawable
                    this.knobBefore = healthDrawable
                }
        ), DisposableByManager, ManuallyJsonSerializable, WithZIndex {
    init {
        backgroundDrawable.withMinSize()
        healthDrawable.withMinSize()
        setSize(backgroundDrawable.size.x, backgroundDrawable.size.y)
        value = maxHealth
        setAnimateDuration(ANIMATE_DURATION)
    }

    override fun act(delta: Float) {
        super.act(delta)
        backgroundDrawable.update(delta)
        healthDrawable.update(delta)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }

    override fun dispose(manager: TextureManager) {
        backgroundDrawable.dispose(manager)
        healthDrawable.dispose(manager)
    }

    override fun serializeUpdates(json: JsonValue) {
        json.addChild("maxHealth", maxValue.toJsonValue())
        json.addChild("value", value.toJsonValue())
    }

    override fun deserializeUpdates(json: JsonValue) {
        setRange(0f, json["maxHealth", float])
        value = json["value", float]
    }

    override fun serializeFull(json: JsonValue) {
        // no operations
    }

    override fun serializeSave(json: JsonValue) {
        // no operations
    }

    companion object {
        const val ANIMATE_DURATION = 0.1f
        const val STEP_SIZE = 0.1f

        @ManualJsonConstructor
        fun getJsonConstructorDefaults(
            @Suppress("UNUSED_PARAMETER") json: JsonValue,
            parsed: MutableMap<String, Pair<Any?, Boolean>>
        ) {
            JsonDefaults.setDefault("zIndex", 0, parsed)
        }
    }
}
