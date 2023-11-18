package com.darvader.smarthome

import android.os.AsyncTask

import java.io.IOException
import java.io.OutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException
import java.util.ArrayList

class EchoClient @Throws(SocketException::class, UnknownHostException::class)
constructor() {
    private val socketBroadCast = DatagramSocket()
    private val socket = DatagramSocket()
    private val addresses: List<InetAddress>

    init {
        socketBroadCast.broadcast = true
        addresses = listAllBroadcastAddresses() // for hotspot broadcast
    }

    fun sendBroadCast(msg: String) {
        for (a in addresses) {
            val buf = msg.toByteArray()
            val asyncTask = SendAsyncBroadcast(a, buf)
            asyncTask.execute()
        }
    }

    fun sendBroadCast(msg: String, address: InetAddress): String? {
        val buf = msg.toByteArray()
        return sendBroadcastInternal(address, buf)
    }

    @Synchronized
    fun sendBroadcastInternal(address: InetAddress, buf: ByteArray): String? {
        val packet = DatagramPacket(buf, buf.size, address, 4210)
        val received: String? = null
        try {
            socketBroadCast.send(packet)
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }

        return received
    }

    @Throws(SocketException::class)
    internal fun listAllBroadcastAddresses(): List<InetAddress> {
        val broadcastList = ArrayList<InetAddress>()
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()

            if (networkInterface.isLoopback || !networkInterface.isUp) {
                continue
            }

            for (a in networkInterface.interfaceAddresses) {
                val broadcast = a.broadcast
                if (broadcast != null) {
                    broadcastList.add(broadcast)
                }
            }
        }
        return broadcastList
    }

    @Throws(UnknownHostException::class)
    fun send(msg: String, address: String) {
        if (address == "") return
        val buf = msg.toByteArray()
        SendAsync(address, buf).execute()
    }

    @Throws(UnknownHostException::class)
    fun send(colors: ByteArray, address: String) {
        SendAsync(address, colors).execute()
    }

    @Throws(UnknownHostException::class)
    private fun sendInternal(address: String, buf: ByteArray) {
        sendInternal(InetAddress.getByName(address), buf)
    }

    @Synchronized
    private fun sendInternal(address: InetAddress, buf: ByteArray) {
        val packet = DatagramPacket(buf, buf.size, address, 4210)
        try {
            socket.send(packet)
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    fun sendBroadCast(colors: ByteArray) {
        for (a in addresses) {
            SendAsyncBroadcast(a, colors).execute()
        }
    }

    fun sendTcp(byteArray: ByteArray, matrixAddress: String) {
        try {
            val clientSocket = Socket(matrixAddress, 80)
            val outToServer: OutputStream = clientSocket.getOutputStream()
            outToServer.write(byteArray)
            clientSocket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class SendAsyncBroadcast(private val address: InetAddress, private val msg: ByteArray) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg strings: String): String? {
            synchronized(this@EchoClient) {
                return sendBroadcastInternal(address, msg)
            }
        }
    }

    private inner class SendAsync(private val address: String, private val colors: ByteArray) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg strings: String): String? {
            synchronized(this@EchoClient) {
                try {
                    sendInternal(address, colors)
                } catch (e: UnknownHostException) {
                    throw IllegalStateException(e)
                }
            }
            return null
        }
    }
}
