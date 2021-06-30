package com.company.todd.screen.menu

import com.company.todd.launcher.ToddGame
import com.company.todd.net.ToddUDPClient
import com.company.todd.screen.game.ClientGameScreen
import java.net.SocketAddress

class ConnectingScreen(game: ToddGame, address: SocketAddress): MenuScreen(game), ToddUDPClient.ClientUpdatesListener {
    private val client = ToddUDPClient(this)
    private var gameScreen: ClientGameScreen? = null
    private var disconnected = false

    init {
        client.start(address)
        screenActors.addActor(label("Connecting...").apply { setFillParent(true) })
    }

    override fun whenConnected(serverData: String) {
        synchronized(this) {
            gameScreen = ClientGameScreen(game, client, serverData)
            client.updatesListener = gameScreen!!
        }
    }

    override fun getServerUpdates(updates: String) {
        // no operations
    }

    override fun onDisconnect() {
        synchronized(this) {
            disconnected = true
        }
    }

    override fun update(delta: Float) {
        synchronized(this) {
            super.update(delta)
            if (disconnected) {
                gameScreen?.dispose()
                // TODO show could not connect message
            } else {
                gameScreen?.let { game.screenManager.replaceAll(it) }
            }
        }
    }

    override fun dispose() {
        synchronized(this) {
            super.dispose()
        }
    }
}