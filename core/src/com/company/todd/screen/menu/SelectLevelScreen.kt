package com.company.todd.screen.menu

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.company.todd.json.deserialization.loadLevels
import com.company.todd.launcher.ToddGame
import com.company.todd.objects.passive.Level

class SelectLevelScreen(game: ToddGame, levelConsumer: (Level) -> Unit) : MenuScreen(game) {
    init {
        val table = Table()
        table.setFillParent(true)
        screenActors.addActor(table)

        loadLevels().take(3).forEach {
            val button = textButton(it.name)
            table.add(button).fillX()
            table.row().pad(10f, 0f, 0f, 0f)
            button.addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: Actor?) {
                    levelConsumer(it)
                }
            })
        }
    }
}
