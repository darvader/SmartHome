package com.darvader.smarthome.ledstrip.christmas

import com.darvader.smarthome.HomeElement
import com.darvader.smarthome.SmartHomeActivity
import com.darvader.smarthome.ledstrip.LedStrip

class Calibrate(calibrateActivity: CalibrateActivity) {
    companion object {
        var currentAddress = LedStrip.currentAddress
        val echoClient = SmartHomeActivity.echoClient
    }

    fun calibrate() {
        for (i in 0..499) {
            val low = (i and 0xff).toByte()
            val high = (i shr 8).toByte()
            val msg = "setPixel=".toByteArray(Charsets.UTF_8) + high + low

            println("setPixel=$high $low")
            echoClient.send(msg, currentAddress)
            Thread.sleep(50)
        }
    }

}
