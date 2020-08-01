package com.company.todd.objects.active.creature.weapon

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.base.worldAABBFor
import com.company.todd.screen.GameScreen
import com.company.todd.util.asset.texture.MyDrawable

abstract class MeleeWeapon(protected val screen: GameScreen,
                           weaponDrawable: MyDrawable?, handDrawable: MyDrawable?,
                           weaponPosition: Vector2?, handPosition: Vector2?,
                           protected val attackAABB: Rectangle) :
        HandWeapon(weaponDrawable, handDrawable, weaponPosition, handPosition) {
    override fun attack() {
        super.attack()
        val attacked = mutableSetOf<InGameObject>()
        worldAABBFor(Rectangle(attackAABB)).let { aabb ->
            screen.queryAABB(aabb.x, aabb.y, aabb.x + aabb.width, aabb.y + aabb.height) {
                if (shouldAttack(it)) {
                    attacked.add(it.body.userData as InGameObject)
                }
                true
            }
        }
        attacked.forEach { it.takeDamage(power) }
    }

    protected abstract fun shouldAttack(fixture: Fixture): Boolean
}
