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

    @Synchronized
    override fun onConnection(serverData: String, ping: Long, receivedAt: Long) {
        gameScreen = ClientGameScreen(game, client, serverData, ping, receivedAt)
        client.updatesListener = gameScreen!!
    }

    override fun onNewPing(ping: Long, timeDeltaWithServer: Long) {
        // no operations
    }

    override fun onServerUpdates(updates: String) {
        // no operations
    }

    @Synchronized
    override fun onDisconnect() {
        disconnected = true
    }

    @Synchronized
    override fun update(delta: Float) {
        super.update(delta)
        if (disconnected) {
            gameScreen?.dispose()
            // TODO show could not connect message
        } else {
            gameScreen?.let { game.screenManager.replaceAll(it) }
        }
    }

    @Synchronized
    override fun dispose() {
        super.dispose()
    }
}
