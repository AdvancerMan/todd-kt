package com.company.todd.net

import java.io.Closeable
import java.lang.IllegalStateException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress
import java.net.SocketTimeoutException
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class ToddUDPClient(private val updatesListener: ClientUpdatesListener) : Closeable {
    private var listeningThread: Thread? = null
    private var sendingThread: Thread? = null
    private var socket: DatagramSocket? = null
    private val updates: BlockingQueue<String> = ArrayBlockingQueue(30)

    fun start(serverAddress: SocketAddress) {
        socket = DatagramSocket()
        if (connect(serverAddress)) {
            listeningThread = Thread { listenUpdates() }.also { it.start() }
            sendingThread = Thread { sendUpdates(serverAddress) }.also { it.start() }
        }
    }

    private fun connect(serverAddress: SocketAddress): Boolean {
        // TODO log IOException
        socket!!.send(DatagramPacket(ToddUDPServer.CONNECT_MESSAGE, ToddUDPServer.CONNECT_MESSAGE.size, serverAddress))

        socket!!.soTimeout = 5000
        val buffer = ByteArray(socket!!.receiveBufferSize)
        val received = DatagramPacket(buffer, buffer.size)

        try {
            socket!!.receive(received)
        } catch (e: SocketTimeoutException) {
            updatesListener.onDisconnect()
            return false
        } finally {
            socket!!.soTimeout = 0
        }
        updatesListener.whenConnected(String(buffer, 0, received.length, Charsets.UTF_8))
        return true
    }

    private fun listenUpdates() {
        val buffer = ByteArray(socket!!.receiveBufferSize)
        val received = DatagramPacket(buffer, buffer.size)
        while (!Thread.currentThread().isInterrupted) {
            // TODO receive blocks send -- big lag
            // TODO log IOException
            socket!!.receive(received)
            if (
                Arrays.equals(
                    buffer, 0, received.length,
                    ToddUDPServer.DISCONNECTED_MESSAGE, 0, ToddUDPServer.DISCONNECTED_MESSAGE.size
                )
            ) {
                updatesListener.onDisconnect()
                break
            } else {
                updatesListener.getServerUpdates(String(buffer, 0, received.length, Charsets.UTF_8))
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

    override fun close() {
        listeningThread?.interrupt()
        sendingThread?.interrupt()
        socket?.close()
        listeningThread?.join()
        sendingThread?.join()
    }

    interface ClientUpdatesListener {
        fun whenConnected(serverData: String)
        fun getServerUpdates(updates: String)
        fun onDisconnect()
    }
}
