package io.github.advancerman.todd.net

import io.github.advancerman.todd.launcher.ToddGame
import io.github.advancerman.todd.util.withExceptionHandler
import java.io.Closeable
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.NetworkInterface

class ToddBroadcastServer(private val message: ByteArray, private val sendPeriodMs: Long) : Closeable {
    private var thread: Thread? = null
    private var socket: DatagramSocket? = null
//    TODO @Volatile var broadcast: Boolean = true

    fun start(game: ToddGame) {
        socket = DatagramSocket()
        thread = Thread({ doBroadcast() }, "Todd broadcast server thread")
            .withExceptionHandler(game.logger)
            .also { it.start() }
    }

    private fun doBroadcast() {
        socket!!.broadcast = true
        while (!Thread.currentThread().isInterrupted) {
            NetworkInterface.getNetworkInterfaces()
                .asSequence()
                .filter { !it.isLoopback }
                .flatMap { it.interfaceAddresses.asSequence() }
                .filter { it.broadcast != null }
                .forEach { address ->
                    val packet = DatagramPacket(
                        message,
                        message.size,
                        address.broadcast,
                        BROADCAST_PORT
                    )
                    socket!!.send(packet)  // TODO log IOException
                }

            try {
                Thread.sleep(sendPeriodMs)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
    }

    override fun close() {
        try {
            thread?.interrupt()
            socket?.close()
            thread?.join()
        } finally {
            socket = null
            thread = null
        }
    }

    companion object {
        const val BROADCAST_PORT = 8980
    }
}
