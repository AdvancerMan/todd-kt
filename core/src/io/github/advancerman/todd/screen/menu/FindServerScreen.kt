package io.github.advancerman.todd.screen.menu

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.net.ToddBroadcastListener
import java.net.SocketAddress

class FindServerScreen(game: ToddGame) : MenuScreen(game), ToddBroadcastListener.ToddServersListener {
    private val broadcastListener = ToddBroadcastListener(this).also { it.start(game) }
    private val servers = mutableListOf<SocketAddress>()
    private val table = Table()

    init {
        table.setFillParent(true)
        screenActors.addActor(table)
        table.pad(10f, 0f, 10f, 0f)
    }

    @Synchronized
    override fun shouldAddServer(address: SocketAddress): Boolean {
        return address !in servers
    }

    @Synchronized
    override fun addServer(address: SocketAddress, info: String) {
        val button = textButton(info)
        table.add(button)
        table.row()
        servers.add(address)
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                game.screenManager.replaceLast(ConnectingScreen(game, address))
            }
        })
    }

    @Synchronized
    override fun render(delta: Float) {
        super.render(delta)
    }

    @Synchronized
    override fun dispose() {
        broadcastListener.close()
        super.dispose()
    }
}
