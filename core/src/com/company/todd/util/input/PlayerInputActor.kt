package com.company.todd.util.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

class PlayerInputActor : Actor() {
    var isMovingLeft = false
        private set
    var isMovingRight = false
        private set
    var isJumping = false
        private set

    fun createListener() =
            object : InputListener() {
                override fun keyUp(event: InputEvent, keycode: Int): Boolean {
                    when (keycode) {
                        Input.Keys.W -> isJumping = false
                        Input.Keys.A -> isMovingLeft = false
                        Input.Keys.D -> isMovingRight = false
                        Input.Keys.UP -> isJumping = false
                        Input.Keys.LEFT -> isMovingLeft = false
                        Input.Keys.RIGHT -> isMovingRight = false
                        else -> return false
                    }
                    return true
                }

                override fun keyDown(event: InputEvent, keycode: Int): Boolean {
                    when (keycode) {
                        Input.Keys.W -> isJumping = true
                        Input.Keys.A -> isMovingLeft = true
                        Input.Keys.D -> isMovingRight = true
                        Input.Keys.UP -> isJumping = true
                        Input.Keys.LEFT -> isMovingLeft = true
                        Input.Keys.RIGHT -> isMovingRight = true
                        else -> return false
                    }
                    return true
                }
            }
}
