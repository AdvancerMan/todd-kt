package com.company.todd.objects.active.creature.enemy

import com.badlogic.gdx.math.Vector2
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.active.creature.RectangleCreature
import com.company.todd.objects.active.creature.weapon.Weapon
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.gui.HealthBar
import com.company.todd.thinker.StupidMeleeThinker

open class StupidEnemy(game: ToddGame, drawable: MyDrawable,
                       drawableSize: Vector2, bodyLowerLeftCornerOffset: Vector2,
                       bodyPosition: Vector2, bodySize: Vector2, weapon: Weapon?,
                       healthBar: HealthBar, speed: Float, jumpPower: Float,
                       jumpCooldown: Float, maxDistanceFromTarget: Float) :
        RectangleCreature(
            game, drawable, drawableSize, bodyLowerLeftCornerOffset, bodyPosition, bodySize, weapon,
            StupidMeleeThinker(maxDistanceFromTarget, jumpCooldown), healthBar, speed, jumpPower)
