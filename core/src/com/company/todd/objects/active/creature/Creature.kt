package com.company.todd.objects.active.creature

import com.badlogic.gdx.math.Vector2
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.ActiveObject
import com.company.todd.objects.active.creature.weapon.Weapon
import com.company.todd.screen.GameScreen
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.gui.HealthBar

abstract class Creature(game: ToddGame, drawable: MyDrawable, drawableSize: Vector2,
                        bodyLowerLeftCornerOffset: Vector2, bodyPattern: BodyPattern,
                        protected var weapon: Weapon?, healthBar: HealthBar,
                        speed: Float, jumpPower: Float) :
        ActiveObject(game, drawable, drawableSize, bodyLowerLeftCornerOffset,
                bodyPattern, healthBar, speed, jumpPower) {
    override fun doInit(gameScreen: GameScreen) {
        super.doInit(gameScreen)
        weapon?.let {
            addActor(it)
            it.init(this, gameScreen)
        }
    }

    fun attack() {
        weapon?.attack()
    }
}
