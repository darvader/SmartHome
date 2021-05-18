package com.darvader.smarthome

import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.util.ArrayList

class EchoServer @Throws(SocketException::class)
constructor() : Thread() {

    private val socket = DatagramSocket(4445)
    private var running = false
    private val buf = ByteArray(256)
    val homeElements = ArrayList<HomeElement>()

    override fun run() {
        running = true

        while (running) {
            try {
                var packet = DatagramPacket(buf, buf.size)
                socket.receive(packet)

                val address = packet.address


                val port = packet.port
                packet = DatagramPacket(buf, buf.size, address, port)
                val received = String(packet.data, 0, packet.length)
                homeElements.forEach {
                    it -> it.refresh(address, received)
                }
                if (received == "end") {
                    running = false
                    continue
                }

                println("IP: " + address.hostAddress + " " + received)
            } catch (e: IOException) {
                throw IllegalStateException(e)
            }

        }
    }

    fun register(element: HomeElement) {
        homeElements.add(element)
    }
}