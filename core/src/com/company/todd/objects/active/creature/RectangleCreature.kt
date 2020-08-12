package com.company.todd.objects.active.creature

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.weapon.Weapon
import com.company.todd.util.asset.texture.MyDrawable
import com.company.todd.util.box2d.bodyPattern.sensor.createRectangleBPWithTGSBGS

abstract class RectangleCreature(
        game: ToddGame, drawable: MyDrawable, bodyPosition: Vector2, bodySize: Vector2,
        drawableSize: Vector2, bodyLowerLeftCornerOffset: Vector2, weapon: Weapon?,
        speed: Float, jumpPower: Float, maxHealth: Float
) :
        Creature(
                game, drawable,
                createRectangleBPWithTGSBGS(
                        BodyDef.BodyType.DynamicBody,
                        bodySize, bodyPosition
                ),
                drawableSize, bodyLowerLeftCornerOffset, weapon, speed, jumpPower, maxHealth
        )
