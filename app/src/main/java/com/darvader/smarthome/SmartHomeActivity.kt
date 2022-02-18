package com.darvader.smarthome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.kitchen_light.KitchenLightActivity
import com.darvader.smarthome.ledstrip.LedStrip
import com.darvader.smarthome.matrix.activity.LedMatrixActivity
import com.darvader.smarthome.ledstrip.LedStripActivity
import com.darvader.smarthome.ledstrip.christmas.ChristmasTreeActivity
import com.darvader.smarthome.livingroomlight.LightsActivity
import kotlinx.android.synthetic.main.activity_smart_home.*

class SmartHomeActivity : AppCompatActivity() {

    companion object {
        val echoServer = EchoServer()
        val echoClient = EchoClient()
        init {
            echoServer.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_home)

        ledstrips.setOnClickListener {
            val intent = Intent(this, LedStripActivity::class.java)
            startActivity(intent)
        }

        ledMatrix.setOnClickListener {
            val intent = Intent(this, LedMatrixActivity::class.java)
            startActivity(intent)
        }

        livingRoomLamp.setOnClickListener {
            val intent = Intent(this, LightsActivity::class.java)
            startActivity(intent)
        }

        christmasTree.setOnClickListener {
            val intent = Intent(this, ChristmasTreeActivity::class.java)
            startActivity(intent)
        }

        kitchen.setOnClickListener {
            val intent = Intent(this, KitchenLightActivity::class.java)
            startActivity(intent)
        }

        allOff.setOnClickListener { allOff() }

    }

    private fun allOff() {
        println("All off called.")
        LedStrip.echoClient.sendBroadCast("off")
    }
}