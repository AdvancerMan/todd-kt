package com.company.todd.objects.passive.interactive

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Manifold
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.asset.texture.MyDrawable
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.json.JsonFullSerializable
import com.company.todd.json.SerializationType

@SerializationType("igo", "jumper")
class Jumper(game: ToddGame, drawable: MyDrawable, bodyPattern: BodyPattern,
             @JsonFullSerializable private val pushPower: Float) :
        HalfCollidedPlatform(game, drawable, bodyPattern) {
    override fun processContact(other: InGameObject, contact: Contact, oldManifold: Manifold) {
        super.processContact(other, contact, oldManifold)
        other.body.setYVelocity(pushPower)
    }
}
