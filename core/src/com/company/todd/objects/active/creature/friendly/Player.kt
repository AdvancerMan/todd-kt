package com.company.todd.objects.active.creature.friendly

import com.badlogic.gdx.math.Vector2
import com.company.todd.gui.HealthBar
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.RectangleCreature
import com.company.todd.input.PlayerThinker

// TODO hardcoded numbers
class Player(game: ToddGame, thinker: PlayerThinker) :
        RectangleCreature(
                game,
                game.textureManager.loadDrawable("player"),
                Vector2(50f, 100f), Vector2(), Vector2(),
                Vector2(50f, 100f), null, thinker,
                HealthBar(
                        100f, 0.1f, 0.1f,
                        game.textureManager.loadDrawable("healthBarBackground"),
                        game.textureManager.loadDrawable("healthBarHealth")
                ), 300f, 500f
        )
