package com.company.todd.screen.menu

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.company.todd.launcher.ToddGame
import com.company.todd.screen.game.DebugScreen
import com.company.todd.screen.game.GameScreen
import com.company.todd.screen.game.ServerGameScreen

class MainMenuScreen(game: ToddGame) : MenuScreen(game) {
    init {
        val table = Table()
        table.setFillParent(true)
        screenActors.addActor(table)

        val debug = textButton("Debug mode")
        val single = textButton("Single player")
        val startServer = textButton("Start server")
        val findServer = textButton("Find server")

        table.add(debug).fillX()
        table.row().pad(10f, 0f, 0f, 0f)
        table.add(single).fillX()
        table.row().pad(10f, 0f, 0f, 0f)
        table.add(startServer).fillX()
        table.row().pad(10f, 0f, 0f, 0f)
        table.add(findServer).fillX()

        debug.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.screenManager.push(SelectLevelScreen(game) {
                    game.screenManager.replaceAll(DebugScreen(game, it))
                })
            }
        })

        single.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.screenManager.push(SelectLevelScreen(game) {
                    game.screenManager.replaceAll(GameScreen(game, it))
                })
            }
        })

        startServer.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.screenManager.push(SelectLevelScreen(game) {
                    game.screenManager.replaceAll(ServerGameScreen(game, "Todd Ethot game server", it))
                })
            }
        })

        findServer.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.screenManager.push(FindServerScreen(game))
            }
        })
    }
}
