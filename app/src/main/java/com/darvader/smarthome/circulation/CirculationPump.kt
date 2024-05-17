package com.darvader.smarthome.circulation

import android.graphics.Color
import com.darvader.smarthome.*
import com.darvader.smarthome.ledstrip.LedStrip
import com.darvader.smarthome.livingroomlight.Lights
import java.net.InetAddress

class CirculationPump(val circulationPumpActivity: CirculationPumpActivity) : HomeElement {

    companion object {
        var circulationAddress = "192.168.0.61"
        val echoClient = SmartHomeActivity.echoClient
    }

    fun off() {
        println("Off called.")
        echoClient.send("off", circulationAddress)
    }

    fun circ5Min() {
            val msg = "duration=".toByteArray(Charsets.UTF_8) + 5.toByte()
            println("duration=5 called")
            Lights.echoClient.send(msg, circulationAddress)
    }

    fun on() {
        println("On called.")
        echoClient.send("on", circulationAddress)
    }

    fun status() {
        println("Status called.")
        echoClient.send("status", circulationAddress)
    }

    fun detect() {
        println("Detect called.")
        LedStrip.echoClient.sendBroadCast("Detect")
    }

    override fun refresh(address: InetAddress, received: String) {
        if (received.startsWith("CirculationOn")) {
            this.circulationPumpActivity.binding.onCirculation.setBackgroundColor(Color.GREEN)
            this.circulationPumpActivity.binding.offCirculation.setBackgroundColor(Color.GREEN)
            this.circulationPumpActivity.binding.circ5Min.setBackgroundColor(Color.GREEN)
        }
        if (received.startsWith("CirculationOff")) {
            this.circulationPumpActivity.binding.onCirculation.setBackgroundColor(Color.RED)
            this.circulationPumpActivity.binding.offCirculation.setBackgroundColor(Color.RED)
            this.circulationPumpActivity.binding.circ5Min.setBackgroundColor(Color.RED)
        }

    }
}
