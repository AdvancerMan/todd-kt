package com.company.todd.objects.creature

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.box2d.bodyPattern.createRectangleBPWithTGSBGS
import com.company.todd.gui.HealthBar
import com.company.todd.json.SerializationType
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.weapon.HandWeapon
import com.company.todd.objects.weapon.SimpleMeleeWeapon
import com.company.todd.thinker.Thinker

// TODO hardcoded numbers
@SerializationType("creature")
class Player(game: ToddGame, thinker: Thinker) :
        Creature(
                game,
                game.textureManager.loadDrawable("player"),
                Vector2(60f, 110f), Vector2(),
                createRectangleBPWithTGSBGS(
                        BodyDef.BodyType.DynamicBody,
                        Vector2(), Vector2(30f, 91f)
                ),
                SimpleMeleeWeapon(
                        HandWeapon.Style(
                                null, game.textureManager.loadDrawable("simpleWeapon"),
                                Vector2(0f, 0f), Vector2(0f, 30f), Vector2(0f, 0f),
                        ),
                        Rectangle(30f, 1f, 20f, 89f),
                        15f, 1f, 0.3f
                ),
                thinker,
                HealthBar(
                        100f,
                        game.textureManager.loadDrawable("healthBarBackground"),
                        game.textureManager.loadDrawable("healthBarHealth")
                ), 300f, 500f
        )
