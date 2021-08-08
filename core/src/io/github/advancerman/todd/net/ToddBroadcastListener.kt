package io.github.advancerman.todd.net

import com.badlogic.gdx.Gdx
import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.util.withExceptionHandler
import java.io.Closeable
import java.io.IOException
import java.lang.NumberFormatException
import java.net.*

class ToddBroadcastListener(private val listener: ToddServersListener) : Closeable {
    private lateinit var socket: DatagramSocket
    private lateinit var thread: Thread
    @Volatile
    private var closed = false

    fun start(game: ToddGame) {
        socket = DatagramSocket(ToddBroadcastServer.BROADCAST_PORT)
        thread = Thread({ run() }, "Todd broadcast listener thread")
            .withExceptionHandler(game.logger)
            .also { it.start() }
    }

    private fun run() {
        val buffer = ByteArray(socket.receiveBufferSize)
        val receivePacket = DatagramPacket(buffer, buffer.size)
        val sendPacket = DatagramPacket(ToddUDPServer.ASK_INFO_MESSAGE, ToddUDPServer.ASK_INFO_MESSAGE.size)

        try {
            while (!Thread.currentThread().isInterrupted) {
                socket.receive(receivePacket)
                val message = buffer.copyOf(receivePacket.length).toString(Charsets.UTF_8)
                getPort(message)?.let { port ->
                    val address = InetSocketAddress(receivePacket.address, port)
                    if (!listener.shouldAddServer(address)) {
                        return@let
                    }

                    sendPacket.socketAddress = address
                    socket.send(sendPacket)
                    socket.soTimeout = INFO_TIMEOUT
                    try {
                        socket.receive(receivePacket)
                    } catch (e: SocketTimeoutException) {
                        return@let
                    } finally {
                        socket.soTimeout = 0
                    }
                    listener.addServer(address, String(receivePacket.data, 0, receivePacket.length, Charsets.UTF_8))
                }
            }
        } catch (e: IOException) {
            if (closed) {
                return
            }
            Gdx.app.error("BroadcastListener", "Exception during server finding", e)
            // TODO maybe callback to GUI ???
        }
    }

    private fun getPort(message: String): Int? {
        if (!message.startsWith(ToddUDPServer.BROADCAST_PREFIX)) {
            return null
        }

        return try {
            val result = message.substring(ToddUDPServer.BROADCAST_PREFIX.length).toInt()
            return if (result in 1 until (1 shl 16)) result else null
        } catch (e: NumberFormatException) {
            null
        }
    }

    override fun close() {
        if (closed) {
            return
        }
        closed = true
        thread.interrupt()
        socket.close()
        thread.join()
    }

    interface ToddServersListener {
        fun shouldAddServer(address: SocketAddress): Boolean
        fun addServer(address: SocketAddress, info: String)
    }

    companion object {
        const val INFO_TIMEOUT = 1000
    }
}
