package com.darvader.smarthome.matrix.livescore

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import javax.net.ssl.SSLSocketFactory

class LiveScoreWebSocketManager {

    companion object {
        private const val TAG = "LiveScoreWebSocket"
        const val WEB_SOCKET_URL_DVV = "wss://backend.sams-ticker.de/indoor/dvv"
        const val WEB_SOCKET_URL_TVV = "wss://backend.sams-ticker.de/indoor/tvv"
    }

    interface WebSocketListener {
        fun onMatchUpdate(payload: JSONObject)
        fun onWebSocketConnected()
        fun onWebSocketDisconnected()
        fun onWebSocketError(error: String)
    }

    private var webSocketClient: WebSocketClient? = null
    private var listener: WebSocketListener? = null
    private var currentWebSocketUrl: String? = null

    fun setListener(listener: WebSocketListener) {
        this.listener = listener
    }

    fun connect(region: String) {
        // Determine WebSocket URL based on region
        currentWebSocketUrl = when {
            region.contains("tvv") -> WEB_SOCKET_URL_TVV
            region.contains("dvv") -> WEB_SOCKET_URL_DVV
            else -> null // No WebSocket for TEST_MODE
        }

        currentWebSocketUrl?.let { url ->
            if (webSocketClient?.isOpen == true) {
                return // Already connected
            }

            disconnect()
            initializeWebSocket(url)
        }
    }

    private fun initializeWebSocket(url: String) {
        try {
            val uri = URI(url)
            createWebSocketClient(uri)

            val socketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
            webSocketClient?.setSocketFactory(socketFactory)
            webSocketClient?.connect()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing WebSocket: ${e.message}")
            listener?.onWebSocketError(e.message ?: "Unknown WebSocket error")
        }
    }

    private fun createWebSocketClient(uri: URI) {
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "WebSocket connected")
                listener?.onWebSocketConnected()
            }

            override fun onMessage(message: String?) {
                handleMessage(message)
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "WebSocket closed: $reason")
                listener?.onWebSocketDisconnected()

                // Auto-reconnect if connection was not intentionally closed
                if (!remote) {
                    reconnect()
                }
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "WebSocket error: ${ex?.message}")
                listener?.onWebSocketError(ex?.message ?: "Unknown WebSocket error")
            }
        }
    }

    private fun handleMessage(message: String?) {
        message?.let {
            try {
                val json = JSONObject(it)
                val type = json.getString("type")

                if (type == "MATCH_UPDATE") {
                    Log.d(TAG, "Received MATCH_UPDATE")
                    val payload = json.getJSONObject("payload")
                    listener?.onMatchUpdate(payload)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing WebSocket message: ${e.message}")
            }
        }
    }

    private fun reconnect() {
        currentWebSocketUrl?.let { url ->
            Log.d(TAG, "Attempting to reconnect WebSocket")
            Thread.sleep(5000) // Wait 5 seconds before reconnecting
            initializeWebSocket(url)
        }
    }

    fun disconnect() {
        webSocketClient?.close()
        webSocketClient = null
    }

    fun isConnected(): Boolean {
        return webSocketClient?.isOpen == true
    }
}
