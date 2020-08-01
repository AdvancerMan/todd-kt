package com.company.todd.objects.active.creature.weapon

import com.badlogic.gdx.scenes.scene2d.Actor

abstract class Weapon : Actor() {
    abstract val power: Float
    abstract fun attack()
}
