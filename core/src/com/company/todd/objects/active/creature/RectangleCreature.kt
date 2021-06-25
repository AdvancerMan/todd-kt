package com.company.todd.objects.active.creature

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.weapon.Weapon
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.sensor.createRectangleBPWithTGSBGS
import com.company.todd.gui.HealthBar
import com.company.todd.objects.active.ActiveObject
import com.company.todd.thinker.Thinker

abstract class RectangleCreature(
        game: ToddGame, drawable: MyDrawable, drawableSize: Vector2,
        bodyLowerLeftCornerOffset: Vector2, bodyPosition: Vector2, bodySize: Vector2,
        weapon: Weapon?, thinker: Thinker, healthBar: HealthBar, speed: Float, jumpPower: Float
) :
        ActiveObject(
                game, drawable, drawableSize, bodyLowerLeftCornerOffset,
                createRectangleBPWithTGSBGS(
                        BodyDef.BodyType.DynamicBody,
                        bodyPosition, bodySize
                ),
                weapon, thinker, healthBar, speed, jumpPower
        )
