package com.company.todd.objects.active.creature.friendly

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.Creature
import com.company.todd.util.box2d.bodyPattern.sensor.createRectangleBPWithTGSBGS
import com.company.todd.util.input.PlayerInputActor

// TODO hardcoded numbers
class Player(game: ToddGame, private val inputActor: PlayerInputActor) :
        Creature(
                game,
                game.textureManager.loadDrawable("player"),
                createRectangleBPWithTGSBGS(BodyDef.BodyType.DynamicBody, Vector2(50f, 100f), Vector2()),
                Vector2(50f, 100f), Vector2(), null, 300f, 500f, 100f
        ) {
    override fun think(delta: Float) {
        if (inputActor.isMovingLeft) {
            isDirectedToRight = false
            run(false)
        }
        if (inputActor.isMovingRight) {
            isDirectedToRight = true
            run(true)
        }
        if (inputActor.isJumping) {
            jump()
        }
    }
}
