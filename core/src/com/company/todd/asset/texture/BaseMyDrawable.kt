package com.company.todd.asset.texture

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable

abstract class BaseMyDrawable : BaseDrawable(), MyDrawable {
    override var myZIndex = 0
    override var drawableName: String?
        get() = name
        set(value) {
            name = value
        }
    override val size = Vector2()
    override val offset = Vector2()
}
