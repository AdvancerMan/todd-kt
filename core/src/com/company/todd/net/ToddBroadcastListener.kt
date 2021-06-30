package com.company.todd.net

import java.io.Closeable
import java.lang.NumberFormatException
import java.net.*

class ToddBroadcastListener(private val listener: ToddServersListener) : Closeable {
    private var socket: DatagramSocket? = null
    private var thread: Thread? = null

    fun start() {
        socket = DatagramSocket(ToddBroadcastServer.BROADCAST_PORT)
        thread = Thread { run() }.also { it.start() }
    }

    fun run() {
        val buffer = ByteArray(socket!!.receiveBufferSize)
        val receivePacket = DatagramPacket(buffer, buffer.size)
        val sendPacket = DatagramPacket(ToddUDPServer.ASK_INFO_MESSAGE, ToddUDPServer.ASK_INFO_MESSAGE.size)
        while (!Thread.currentThread().isInterrupted) {
            // TODO log IOException
            socket!!.receive(receivePacket)
            val message = buffer.copyOf(receivePacket.length).toString(Charsets.UTF_8)
            getPort(message)?.let { port ->
                val address = InetSocketAddress(receivePacket.address, port)
                if (!listener.shouldAddServer(address)) {
                    return@let
                }

                sendPacket.socketAddress = address
                // TODO log IOException
                socket!!.send(sendPacket)
                socket!!.soTimeout = INFO_TIMEOUT
                try {
                    // TODO log IOException
                    socket!!.receive(receivePacket)
                } catch (e: SocketTimeoutException) {
                    return@let
                } finally {
                    socket!!.soTimeout = 0
                }
                listener.addServer(address, String(receivePacket.data, 0, receivePacket.length, Charsets.UTF_8))
            }
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
        thread?.interrupt()
        socket?.close()
        thread?.join()
    }

    interface ToddServersListener {
        fun shouldAddServer(address: SocketAddress): Boolean
        fun addServer(address: SocketAddress, info: String)
    }

    companion object {
        const val INFO_TIMEOUT = 1000
    }
}
