package com.darvader.smarthome.booster

import com.darvader.smarthome.*
import java.net.InetAddress

class Booster(private val boosterActivity: BoosterActivity) : HomeElement {

    companion object {
        var address = "192.168.0.11"
        val echoClient = SmartHomeActivity.echoClient
    }

    fun off() {
        println("Off called.")
        echoClient.send("off", address)
    }

    fun on() {
        println("On called.")
        echoClient.send("on", address)
    }

    fun status() {
        println("Status called.")
        echoClient.send("status", address)
    }

    fun detect() {
        println("Detect called.")
        echoClient.sendBroadCast("Detect")
    }

    fun changeBooster(intensity: Int) {
        println("Turn lamps on: $intensity")
        val msg = "pwm=".toByteArray(Charsets.UTF_8) + intensity.toByte()
        println("pwm=$intensity")
        echoClient.send(msg, address)
    }

    override fun refresh(address: InetAddress, received: String) {
        if (received.startsWith("statusBooster=")) {
            // Remove the prefix
            val dataWithoutPrefix = received.substring("statusBooster=".length, "statusBooster=".length + 13)

            // Split the remaining data into parts
            val parts = dataWithoutPrefix.split(",")
            val temp = parts[0].trim().toFloat()
            val humidity = parts[1].trim().toFloat()

            val result = "Temp: $temp; Humidity: $humidity"
            println(result)
            boosterActivity.runOnUiThread {this.boosterActivity.binding.tempText.setText(result)}
        }
    }

}
