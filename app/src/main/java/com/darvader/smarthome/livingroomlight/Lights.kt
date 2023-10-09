package com.darvader.smarthome.livingroomlight

import com.darvader.smarthome.*
import com.darvader.smarthome.ledstrip.LedStrip
import java.net.InetAddress

class Lights(): HomeElement {

    companion object {
        var livLampAddress = ""
        var bedLightAddress = ""
        var kitchenLightAddress = ""
        val echoClient = SmartHomeActivity.echoClient
    }

    fun off() {
        println("Off called.")
        echoClient.send("off", livLampAddress)
    }

    var bedLightOn = false
    var kitchenLightOn = false

    override fun refresh(address: InetAddress, received: String) {
        if (received.startsWith("LivingRoomLamp")) {
            livLampAddress = address.hostAddress
        }
        if (received.startsWith("BedLightDimmer")) {
            bedLightAddress = address.hostAddress
        }
        if (received.startsWith("KitchenDeskLight")) {
            kitchenLightAddress = address.hostAddress
        }
    }

    fun turnLivLightsOn(numLamps: Int) {
        println("Turn lamps on: $numLamps")
        echoClient.send("numLamps=${numLamps.toChar()}", livLampAddress)
    }

    fun switchBedLight() {
        println("Turn bed on.")
        if (bedLightOn)
            echoClient.send("off", bedLightAddress)
        else
            echoClient.send("on", bedLightAddress)
        bedLightOn = !bedLightOn
    }

    fun switchKitchenLight() {
        println("Turn kitchen light on.")
        if (kitchenLightOn)
            echoClient.send("off", kitchenLightAddress)
        else
            echoClient.send("on", kitchenLightAddress)
        kitchenLightOn = !kitchenLightOn
    }

    fun changeBed(intensity: Int) {
        println("Turn lamps on: $intensity")
        val msg = "dimm=".toByteArray(Charsets.UTF_8) + intensity.toByte()
        println("dimm=$intensity")
        echoClient.send(msg, bedLightAddress)
    }

    fun changeKitchen(intensity: Int) {
        println("Turn lamps on: $intensity")
        val msg = "dimm=".toByteArray(Charsets.UTF_8) + intensity.toByte()
        println("dimm=$intensity")
        echoClient.send(msg, kitchenLightAddress)
    }

    fun detect() {
        println("Detect called.")
        LedStrip.echoClient.sendBroadCast("Detect")
    }

}
