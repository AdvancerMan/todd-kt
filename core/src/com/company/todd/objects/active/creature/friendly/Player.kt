package com.company.todd.objects.active.creature.friendly

import com.badlogic.gdx.math.Vector2
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.gui.HealthBar
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.RectangleCreature
import com.company.todd.input.PlayerInputActor

// TODO hardcoded numbers
class Player(game: ToddGame, private val inputActor: PlayerInputActor) :
        RectangleCreature(
                game,
                game.textureManager.loadDrawable("player"),
                Vector2(50f, 100f), Vector2(), Vector2(), Vector2(50f, 100f), null,
                HealthBar(
                        100f, 0.1f, 0.1f,
                        game.textureManager.loadDrawable("healthBarBackground"),
                        game.textureManager.loadDrawable("healthBarHealth")
                ), 300f, 500f
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
