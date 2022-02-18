package com.darvader.smarthome.kitchen_light

import com.darvader.smarthome.*
import com.darvader.smarthome.ledstrip.LedStrip
import com.darvader.smarthome.ledstrip.christmas.Calibrate
import java.net.InetAddress

class KitchenLight(): HomeElement {

    companion object {
        var currentAddress = ""
        val echoClient = SmartHomeActivity.echoClient
    }

    fun off() {
        println("Off called.")
        echoClient.send("off", currentAddress)
    }

    fun on() {
        println("On called.")
        echoClient.send("on", currentAddress)
    }

    override fun refresh(address: InetAddress, received: String) {
        println("Received: $received")
        if (received.startsWith("KitchenDeskLight")) {
            println("Found Kitchen")
            currentAddress = address.hostAddress
        }
    }


    fun detect() {
        println("Detect called.")
        echoClient.sendBroadCast("Detect")
    }

}
