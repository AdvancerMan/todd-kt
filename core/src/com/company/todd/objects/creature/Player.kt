package com.company.todd.objects.creature

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.box2d.bodyPattern.sensor.createRectangleBPWithTGSBGS
import com.company.todd.gui.HealthBar
import com.company.todd.launcher.ToddGame
import com.company.todd.thinker.PlayerThinker

// TODO hardcoded numbers
class Player(game: ToddGame, thinker: PlayerThinker) :
        Creature(
                game,
                game.textureManager.loadDrawable("player"),
                Vector2(50f, 100f), Vector2(),
                createRectangleBPWithTGSBGS(
                        BodyDef.BodyType.DynamicBody,
                        Vector2(), Vector2(50f, 100f)
                ), null, thinker,
                HealthBar(
                        100f, 0.1f, 0.1f,
                        game.textureManager.loadDrawable("healthBarBackground"),
                        game.textureManager.loadDrawable("healthBarHealth")
                ), 300f, 500f
        )
