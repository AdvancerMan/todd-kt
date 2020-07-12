package com.company.todd.objects.active.creature.friendly

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.Creature
import com.company.todd.objects.base.RealBodyWrapper
import com.company.todd.util.box2d.bodyPattern.RectangleBodyPattern
import com.company.todd.util.input.PlayerInputActor

// TODO hardcoded numbers
class Player(game: ToddGame, private val inputActor: PlayerInputActor) :
        Creature(
                game,
                game.textureManager.loadSprite("player"),
                RealBodyWrapper(RectangleBodyPattern(BodyDef.BodyType.DynamicBody, Vector2(50f, 100f))),
                300f,
                500f
        ) {
    override fun think(delta: Float) {
        if (inputActor.isMovingLeft) {
            sprite.isDirectedToRight = false
            run(false)
        }
        if (inputActor.isMovingRight) {
            sprite.isDirectedToRight = true
            run(true)
        }
        if (inputActor.isJumping) {
            jump()
        }
    }
}
