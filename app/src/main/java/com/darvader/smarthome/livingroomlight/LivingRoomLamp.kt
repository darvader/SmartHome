package com.darvader.smarthome.livingroomlight

import com.darvader.smarthome.*
import com.darvader.smarthome.ledstrip.LedStrip
import java.net.InetAddress

class LivingRoomLamp(): HomeElement {

    companion object {
        var currentAddress = ""
        val echoClient = SmartHomeActivity.echoClient
    }

    fun off() {
        println("Off called.")
        echoClient.send("off", currentAddress)
    }

    override fun refresh(address: InetAddress, received: String) {
        if (received.startsWith("LivingRoomLamp")) {
            currentAddress = address.hostAddress
        }
    }

    fun turnLampsOn(numLamps: Int) {
        println("Turn lamps on: $numLamps")
        echoClient.send("numLamps=${numLamps.toChar()}", currentAddress)
    }

    fun detect() {
        println("Detect called.")
        LedStrip.echoClient.sendBroadCast("Detect")
    }

}
