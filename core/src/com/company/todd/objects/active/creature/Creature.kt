package com.company.todd.objects.active.creature

import com.badlogic.gdx.math.Vector2
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.ActiveObject
import com.company.todd.objects.active.creature.weapon.Weapon
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.box2d.bodyPattern.base.BodyPattern

abstract class Creature(game: ToddGame, drawable: MyDrawable,
                        bodyPattern: BodyPattern, drawableSize: Vector2,
                        bodyLowerLeftCornerOffset: Vector2, protected var weapon: Weapon?,
                        speed: Float, jumpPower: Float, maxHealth: Float) :
        ActiveObject(game, drawable, bodyPattern, drawableSize,
                bodyLowerLeftCornerOffset, speed, jumpPower, maxHealth) {
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
