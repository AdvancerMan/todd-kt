package io.github.advancerman.todd.objects.weapon

import com.badlogic.gdx.scenes.scene2d.Actor
import io.github.advancerman.todd.json.JsonFullSerializable
import io.github.advancerman.todd.objects.base.InGameObject
import io.github.advancerman.todd.screen.PostUpdatable
import io.github.advancerman.todd.screen.game.GameScreen

abstract class Weapon : Actor(), PostUpdatable {
    @JsonFullSerializable
    abstract val power: Float
    open fun init(owner: InGameObject, screen: GameScreen) {}
    abstract fun canAttack(): Boolean
    abstract fun attack()
    override fun postUpdate(delta: Float) {}
}
