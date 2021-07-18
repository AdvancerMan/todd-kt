package com.company.todd.gui

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.asset.texture.DisposableByManager
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.asset.texture.TextureManager
import com.company.todd.asset.texture.WithZIndex
import com.company.todd.json.*
import com.company.todd.json.deserialization.float
import com.company.todd.json.deserialization.get
import com.company.todd.json.serialization.toJsonValue

@SerializationType("healthBar")
class HealthBar(
        maxHealth: Float, private val background: MyDrawable,
        private val healthDrawable: MyDrawable,
        @JsonFullSerializable("zIndex") override val myZIndex: Int
) :
        ProgressBar(
                0f, maxHealth, STEP_SIZE, false,
                ProgressBarStyle().apply {
                    this.background = background
                    this.knobBefore = healthDrawable
                }
        ), DisposableByManager, ManuallyJsonSerializable, WithZIndex {
    init {
        background.minHeight = 10f
        background.minWidth = 0f
        healthDrawable.minHeight = 10f
        healthDrawable.minWidth = 0f
        value = maxHealth
        setAnimateDuration(ANIMATE_DURATION)
    }

    override fun act(delta: Float) {
        super.act(delta)
        background.update(delta)
        healthDrawable.update(delta)
    }

    override fun dispose(manager: TextureManager) {
        background.dispose(manager)
        healthDrawable.dispose(manager)
    }

    @JsonFullSerializable("backgroundDrawableName")
    private fun getBackgroundDrawableName() = background.drawableName

    @JsonFullSerializable("healthDrawableName")
    private fun getHealthDrawableName() = healthDrawable.drawableName

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
