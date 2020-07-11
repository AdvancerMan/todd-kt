package com.company.todd.objects.base

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Disposable
import com.company.todd.launcher.ToddGame
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.MySprite

private var maxID = 0

private fun getNewID() = maxID++

abstract class InGameObject(protected val game: ToddGame, protected val sprite: MySprite,
                            protected val body: BodyWrapper): Group(), Disposable {
    private val id: Int = getNewID()
    var initialized = false
        private set
    protected lateinit var screen: GameScreen
    var alive = true
        private set

    fun init(screen: GameScreen) {
        if (!initialized) {
            initialized = true
            this.screen = screen
            body.init(screen)
            sprite.rotation = body.getAngle()
            body.setOwner(this)
        }
    }

    override fun act(delta: Float) {
        sprite.update(delta)
        super.act(delta)
    }

    open fun postAct(delta: Float) {
        if (!body.isFixedRotation()) {
            sprite.rotation = body.getAngle()
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        sprite.setAlpha(parentAlpha)
        sprite.draw(body.getCenter(), batch)
        super.draw(batch, parentAlpha)
    }

    fun isDirectedToRight() = sprite.isDirectedToRight

    fun setDirectedToRight(value: Boolean) {
        sprite.isDirectedToRight = value
    }

    open fun takeDamage(amount: Float) {}

    open fun beginContact(other: InGameObject, contact: Contact) {}
    open fun endContact(other: InGameObject, contact: Contact) {}
    open fun preSolve(other: InGameObject, contact: Contact, oldManifold: Manifold) {}
    open fun postSolve(other: InGameObject, contact: Contact, impulse: ContactImpulse) {}

    override fun equals(other: Any?) =
            other is InGameObject && hashCode() == other.hashCode()

    override fun hashCode() = id

    fun kill() {
        alive = false
    }

    override fun dispose() {
        if (initialized) {
            body.destroy(screen.world)
        }
        sprite.dispose(game.textureManager)
    }

    fun getCenter() = body.getCenter()
}
