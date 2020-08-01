package com.company.todd.objects.active.creature.weapon

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.MyDrawable

class SimpleMeleeWeapon(screen: GameScreen,
                        weaponDrawable: MyDrawable, handDrawable: MyDrawable,
                        weaponPosition: Vector2, handPosition: Vector2,
                        attackAABB: Rectangle, powerAmount: Float) :
        MeleeWeapon(screen, weaponDrawable, handDrawable, weaponPosition, handPosition, attackAABB) {
    override var power = powerAmount
    override fun shouldAttack(fixture: Fixture) = true
}
