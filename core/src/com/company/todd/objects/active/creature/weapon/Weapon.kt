package com.company.todd.objects.active.creature.weapon

import com.badlogic.gdx.scenes.scene2d.Actor
import com.company.todd.objects.base.InGameObject
import com.company.todd.screen.GameScreen

abstract class Weapon : Actor() {
    abstract val power: Float
    open fun init(owner: InGameObject, screen: GameScreen) {}
    abstract fun attack()
}
