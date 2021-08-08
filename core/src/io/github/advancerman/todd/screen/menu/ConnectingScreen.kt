package io.github.advancerman.todd.screen.menu

import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.net.ToddUDPClient
import io.github.advancerman.todd.screen.game.ClientGameScreen
import java.net.SocketAddress

class ConnectingScreen(game: ToddGame, address: SocketAddress): MenuScreen(game), ToddUDPClient.ClientUpdatesListener {
    private val client = ToddUDPClient(this)
    private var gameScreen: ClientGameScreen? = null
    private var disconnected = false

    init {
        client.start(game, address)
        screenActors.addActor(label("Connecting...").apply { setFillParent(true) })
    }

    @Synchronized
    override fun onConnection(serverData: String, ping: Long) {
        gameScreen = ClientGameScreen(game, client, serverData, ping)
        client.updatesListener = gameScreen!!
    }

    override fun onNewPing(ping: Long) {
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
