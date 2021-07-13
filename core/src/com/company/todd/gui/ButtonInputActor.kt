package com.company.todd.gui

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.JsonValue
import com.company.todd.asset.texture.MyDrawable

open class ButtonInputActor(
    jsonSettings: JsonValue,
    resources: Map<String, Map<String, MyDrawable>>,
    jsonName: String, private val onChangedEvent: (Boolean) -> Unit
) : ScreenInputActor<Button>(
    Button(resources[jsonName]!!["up"]!!, resources[jsonName]!!["down"]!!),
    jsonSettings[jsonName]
) {
    override fun changed(event: ChangeListener.ChangeEvent) {
        onChangedEvent(actor.isPressed)
    }
}
