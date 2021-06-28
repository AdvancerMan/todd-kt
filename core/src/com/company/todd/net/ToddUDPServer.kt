package com.company.todd.net

import java.io.Closeable
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress
import java.util.*

class ToddUDPServer(
    private val updatesListener: ServerUpdatesListener,
    private val serverInfo: ByteArray,
    private val sendIntervalMs: Long = 33,
    private val afkTimeBeforeKickingMs: Long = 60_000
) : Closeable {
    private var broadcastServer: ToddBroadcastServer? = null

    private var listeningThread: Thread? = null
    private var sendUpdatesThread: Thread? = null
    private var socket: DatagramSocket? = null

    private var lastActiveMoment: MutableMap<SocketAddress, Long> = mutableMapOf()

    fun start() {
        socket = DatagramSocket()
        listeningThread = Thread { listen() }.also { it.start() }

        sendUpdatesThread = Thread { sendUpdates() }.also { it.start() }

        broadcastServer = ToddBroadcastServer(
            (BROADCAST_PREFIX + socket!!.localPort.toString()).toByteArray(), BROADCAST_PERIOD
        ).also { it.start() }
    }

    private fun sendUpdates() {
        while (!Thread.currentThread().isInterrupted) {
            val updates = updatesListener.flushServerUpdates().toByteArray()
            val packet = DatagramPacket(updates, updates.size)
            val disconnected = mutableListOf<SocketAddress>()
            synchronized(lastActiveMoment) {
                val iterator = lastActiveMoment.iterator()
                while (iterator.hasNext()) {
                    val (address, lastActive) = iterator.next()
                    if (System.currentTimeMillis() - lastActive >= afkTimeBeforeKickingMs) {
                        disconnected.add(address)
                        iterator.remove()
                    } else {
                        // TODO log IOException
                        socket!!.send(packet.also { it.socketAddress = address })
                    }
                }
            }
            disconnected.forEach { updatesListener.onDisconnect(it) }

            try {
                Thread.sleep(sendIntervalMs)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
        }
    }

    private fun connect(received: DatagramPacket) {
        val state = updatesListener.getOnConnectInfo(received.socketAddress).toByteArray()
        // TODO log IOException
        socket!!.send(DatagramPacket(state, state.size, received.socketAddress))
        synchronized(lastActiveMoment) {
            lastActiveMoment[received.socketAddress] = System.currentTimeMillis()
        }
    }

    private fun receiveUpdates(socketAddress: SocketAddress, updates: String) {
        synchronized(lastActiveMoment) {
            lastActiveMoment[socketAddress] = System.currentTimeMillis()
        }
        updatesListener.receiveClientUpdates(socketAddress, updates)
    }

    private fun listen() {
        val buffer = ByteArray(socket!!.receiveBufferSize)
        while (!Thread.currentThread().isInterrupted) {
            val received = DatagramPacket(buffer, buffer.size)
            // TODO log IOException
            socket!!.receive(received)

            when {
                // found no replacement for Array.equals in kotlin
                Arrays.equals(
                    buffer, received.offset, received.length, CONNECT_MESSAGE, 0, CONNECT_MESSAGE.size
                ) -> {
                    connect(received)
                }
                Arrays.equals(
                    buffer, received.offset, received.length, ASK_INFO_MESSAGE, 0, ASK_INFO_MESSAGE.size
                ) -> {
                    // TODO log IOException
                    socket!!.send(DatagramPacket(serverInfo, serverInfo.size, received.socketAddress))
                }
                received.socketAddress in lastActiveMoment.keys -> {
                    receiveUpdates(received.socketAddress, buffer.copyOf(received.length).toString(Charsets.UTF_8))
                }
                else -> {
                    // TODO log IOException
                    socket!!.send(DatagramPacket(
                        DISCONNECTED_MESSAGE, DISCONNECTED_MESSAGE.size, received.socketAddress
                    ))
                }
            }
        }
    }

    override fun close() {
        listeningThread?.interrupt()
        sendUpdatesThread?.interrupt()
        try {
            socket?.close()
        } finally {
            broadcastServer?.close()
        }

        listeningThread?.join()
        sendUpdatesThread?.join()
    }

    companion object {
        const val BROADCAST_PERIOD: Long = 1000
        const val BROADCAST_PREFIX: String = "TODD BROADCAST PREFIX"
        val ASK_INFO_MESSAGE: ByteArray = "GIVE ME TODD INFO".toByteArray()
        val CONNECT_MESSAGE: ByteArray = "HEY, TODD, CONNECT ME".toByteArray()
        val DISCONNECTED_MESSAGE: ByteArray = "HI, TODD, YOU DISCONNECTED!".toByteArray()
    }

    interface ServerUpdatesListener {
        fun receiveClientUpdates(socketAddress: SocketAddress, updates: String)
        fun flushServerUpdates(): String
        fun getOnConnectInfo(socketAddress: SocketAddress): String
        fun onDisconnect(socketAddress: SocketAddress)
    }
}
