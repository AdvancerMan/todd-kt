package com.company.todd.objects.active

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.BodyWrapper
import com.company.todd.objects.base.InGameObject
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.MySprite

// TODO the can i jump question

abstract class ActiveObject(game: ToddGame, sprite: MySprite, body: BodyWrapper,
                            private var speed: Float, private var jumpPower: Float) :
        InGameObject(game, sprite, body) {
    private val velocity = Vector2()

    abstract fun think(delta: Float)

    override fun act(delta: Float) {
        super.act(delta)

        velocity.setZero()
        think(delta)
        updateXVelocity()
        if (!velocity.epsilonEquals(velocity.x, 0f)) {
            updateYVelocity()
        }
    }

    fun jump() {
        velocity.y = jumpPower
    }

    fun run() {
        run(isDirectedToRight())
    }

    fun run(toRight: Boolean) {
        velocity.x += if (toRight) speed else -speed
    }

    protected fun updateXVelocity() {
        body.applyLinearImpulseToCenter(Vector2(velocity.x - body.getVelocity().x, 0f))
    }

    protected fun updateYVelocity() {
        body.setYVelocity(velocity.y)
    }
}
