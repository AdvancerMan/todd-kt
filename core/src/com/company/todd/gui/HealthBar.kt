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
/**
 * Basic health bar for InGameObject.
 * @param maxHealth Maximal health for the owner of health bar.
 * @param backgroundDrawable Background drawable. Z-index is not used,
 *                           size is used for health bar size, offset is not used.
 * @param healthDrawable Foreground drawable. Z-index is not used,
 *                       height is used for foreground height limited to health bar height,
 *                       offset is not used.
 * @param zIndex Z-index relative to owner's actor.
 */
@SerializationType(HealthBar::class)
class HealthBar(
    maxHealth: Float,
    @JsonFullSerializable private val backgroundDrawable: ToddDrawable,
    @JsonFullSerializable private val healthDrawable: ToddDrawable,
    @JsonFullSerializable("zIndex") override val myZIndex: Int = 0
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
    }
}
