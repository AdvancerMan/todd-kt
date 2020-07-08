package com.company.todd.objects.base

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.utils.Disposable
import com.company.todd.launcher.ToddGame
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.MySprite

// TODO dirToRight
// TODO flip sprite

// TODO alive
// TODO isGroundFor ???

private var maxID = 0

fun getNewID() = maxID++

abstract class InGameObject(protected val game: ToddGame, protected val screen: GameScreen,
                            protected val sprite: MySprite, protected val body: BodyWrapper): Disposable {
    private val id: Int = getNewID()

    init {
        sprite.rotation = body.getAngle()
    }

    fun init() {
        body.init(screen)
        body.setOwner(this)
    }

    open fun preUpdate(delta: Float) {
        sprite.update(delta)
    }

    open fun postUpdate(delta: Float) {
        if (!body.isFixedRotation()) {
            sprite.rotation = body.getAngle()
        }
    }

    open fun draw(batch: Batch, cameraRect: Rectangle) {
        sprite.draw(body.getCenter(), batch, cameraRect)
    }

    open fun takeDamage(amount: Float) {}

    open fun beginContact(other: InGameObject, contact: Contact) {}
    open fun endContact(other: InGameObject, contact: Contact) {}
    open fun preSolve(other: InGameObject, contact: Contact, oldManifold: Manifold) {}
    open fun postSolve(other: InGameObject, contact: Contact, impulse: ContactImpulse) {}

    override fun equals(other: Any?) =
            other is InGameObject && (other === this || hashCode() == other.hashCode())

    override fun hashCode() = id

    override fun dispose() {
        body.destroy(screen.world)
        sprite.dispose(game.textureManager)
    }
}
