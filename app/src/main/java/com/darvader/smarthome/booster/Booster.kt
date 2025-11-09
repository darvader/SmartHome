package com.darvader.smarthome.booster

import com.darvader.smarthome.*
import java.net.InetAddress

class Booster(private val boosterActivity: BoosterActivity) : HomeElement {

    companion object {
        var booster1Address = "192.168.0.94"
        var booster2Address = "192.168.0.11"
        val echoClient = SmartHomeActivity.echoClient
    }

    fun off() {
        println("Off called.")
        echoClient.send("off", booster1Address)
    }

    fun off2() {
        println("Off called.")
        echoClient.send("off", booster2Address)
    }

    fun on() {
        println("On called.")
        echoClient.send("on", booster1Address)
    }

    fun on2() {
        println("On called.")
        echoClient.send("on", booster2Address)
    }

    fun status() {
        println("Status called.")
        echoClient.send("status", booster1Address)
    }

    fun status2() {
        println("Status called.")
        echoClient.send("status", booster2Address)
    }

    fun detect() {
        println("Detect called.")
        echoClient.sendBroadCast("Detect")
    }

    fun changeBooster(intensity: Int) {
        println("Turn lamps on: $intensity")
        val msg = "pwm=".toByteArray(Charsets.UTF_8) + intensity.toByte()
        println("pwm=$intensity")
        echoClient.send(msg, booster1Address)
    }

    fun changeBooster2(intensity: Int) {
        println("Turn lamps on: $intensity")
        val msg = "pwm=".toByteArray(Charsets.UTF_8) + intensity.toByte()
        println("pwm=$intensity")
        echoClient.send(msg, booster2Address)
    }

    override fun refresh(address: InetAddress, received: String) {
        if (received.startsWith("statusBooster=")) {
            // Remove the prefix
            val dataWithoutPrefix = received.substring("statusBooster=".length, "statusBooster=".length + 17)

            // Split the remaining data into parts
            val parts = dataWithoutPrefix.split(",")
            val temp = parts[0].trim().toFloat()
            val humidity = parts[1].trim().toFloat()
            val pwm = parts[2].trim().toFloat()

            val result = "Temp: $temp; Humidity: $humidity; Pwm: $pwm"
            println(result)
            if (address.hostAddress == booster1Address)
                boosterActivity.runOnUiThread {this.boosterActivity.binding.tempText.setText(result)}
            else if (address.hostAddress == booster2Address)
                boosterActivity.runOnUiThread {this.boosterActivity.binding.tempText2.setText(result)}
        }
    }

}
