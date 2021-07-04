package com.company.todd.net

import com.company.todd.util.contentEquals
import java.io.Closeable
import java.lang.IllegalStateException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress
import java.net.SocketTimeoutException
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class ToddUDPClient(@Volatile var updatesListener: ClientUpdatesListener) : Closeable {
    private var listeningThread: Thread? = null
    private var sendingThread: Thread? = null
    private var socket: DatagramSocket? = null
    private val updates: BlockingQueue<String> = ArrayBlockingQueue(30)

    private var pingThread: Thread? = null
    private var pingSocket: DatagramSocket? = null

    fun start(serverAddress: SocketAddress) {
        socket = DatagramSocket()
        if (connect(serverAddress)) {
            listeningThread = Thread { listenUpdates() }.also { it.start() }
            sendingThread = Thread { sendUpdates(serverAddress) }.also { it.start() }

            pingSocket = DatagramSocket()
            pingThread = Thread { pingServer(serverAddress) }.also { it.start() }
        } else {
            socket!!.close()
        }
    }

    private fun connect(serverAddress: SocketAddress): Boolean {
        // TODO log IOException
        socket!!.send(DatagramPacket(ToddUDPServer.CONNECT_MESSAGE, ToddUDPServer.CONNECT_MESSAGE.size, serverAddress))

        socket!!.soTimeout = 5000
        val buffer = ByteArray(socket!!.receiveBufferSize)
        val received = DatagramPacket(buffer, buffer.size)

        val before = System.currentTimeMillis()
        try {
            socket!!.receive(received)
        } catch (e: SocketTimeoutException) {
            updatesListener.onDisconnect()
            return false
        } finally {
            socket!!.soTimeout = 0
        }
        val after = System.currentTimeMillis()
        updatesListener.onConnection(
            String(buffer, 0, received.length, Charsets.UTF_8),
            (after - before) / 2, after
        )
        return true
    }

    private fun listenUpdates() {
        val buffer = ByteArray(socket!!.receiveBufferSize)
        val received = DatagramPacket(buffer, buffer.size)
        while (!Thread.currentThread().isInterrupted) {
            // TODO receive blocks send -- big lag
            // TODO log IOException
            socket!!.receive(received)
            if (buffer.contentEquals(received.offset, received.length, ToddUDPServer.DISCONNECTED_MESSAGE)) {
                updatesListener.onDisconnect()
                break
            } else {
                updatesListener.onServerUpdates(String(buffer, 0, received.length, Charsets.UTF_8))
            }
        }
    }

    private fun sendUpdates(serverAddress: SocketAddress) {
        while (!Thread.currentThread().isInterrupted) {
            val message = updates.take().toByteArray(Charsets.UTF_8)
            // TODO log IOException
            socket!!.send(DatagramPacket(message, message.size, serverAddress))
        }
    }

    fun sendUpdate(update: String) {
        if (updates.remainingCapacity() == 0) {
            throw IllegalStateException("Sending too many updates")  // TODO say to user
        }
        updates.put(update)
    }

    fun pingServer(serverAddress: SocketAddress) {
        val pingPacket = DatagramPacket(
            ToddUDPServer.PING_MESSAGE, ToddUDPServer.PING_MESSAGE.size, serverAddress
        )
        val buffer = ByteArray(pingSocket!!.receiveBufferSize)
        val received = DatagramPacket(buffer, buffer.size)
        while (!Thread.currentThread().isInterrupted) {
            val before = System.currentTimeMillis()
            // TODO log IOException
            pingSocket!!.send(pingPacket)
            pingSocket!!.receive(received)
            val after = System.currentTimeMillis()
            // TODO validate data
            val serverTime = String(buffer, 0, received.length, Charsets.UTF_8).toLong()
            val ping = (after - before) / 2
            updatesListener.onNewPing(ping, serverTime - (after - ping))

            try {
                Thread.sleep(PING_PERIOD)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    override fun close() {
        listeningThread?.interrupt()
        sendingThread?.interrupt()
        socket?.close()
        listeningThread?.join()
        sendingThread?.join()
    }

    interface ClientUpdatesListener {
        fun onConnection(serverData: String, ping: Long, receivedAt: Long)
        fun onNewPing(ping: Long, timeDeltaWithServer: Long)
        fun onServerUpdates(updates: String)
        fun onDisconnect()
    }

    companion object {
        const val PING_PERIOD = 1000L
    }
}
