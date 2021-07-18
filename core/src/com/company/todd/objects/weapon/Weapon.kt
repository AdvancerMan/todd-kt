package com.company.todd.objects.weapon

import com.badlogic.gdx.scenes.scene2d.Actor
import com.company.todd.json.JsonFullSerializable
import com.company.todd.objects.base.InGameObject
import com.company.todd.screen.PostUpdatable
import com.company.todd.screen.game.GameScreen

abstract class Weapon : Actor(), PostUpdatable {
    @JsonFullSerializable
    abstract val power: Float
    open fun init(owner: InGameObject, screen: GameScreen) {}
    abstract fun canAttack(): Boolean
    abstract fun attack()
    override fun postUpdate(delta: Float) {}
}
