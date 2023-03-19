package com.darvader.smarthome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.databinding.ActivitySmartHomeBinding
import com.darvader.smarthome.kitchen_light.KitchenLightActivity
import com.darvader.smarthome.ledstrip.LedStrip
import com.darvader.smarthome.matrix.activity.LedMatrixActivity
import com.darvader.smarthome.ledstrip.LedStripActivity
import com.darvader.smarthome.ledstrip.christmas.ChristmasTreeActivity
import com.darvader.smarthome.livingroomlight.LightsActivity

class SmartHomeActivity : AppCompatActivity() {

    companion object {
        val echoServer = EchoServer()
        val echoClient = EchoClient()
        init {
            echoServer.start()
        }
    }

    private lateinit var binding: ActivitySmartHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_home)

        binding = ActivitySmartHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.ledstrips.setOnClickListener {
            val intent = Intent(this, LedStripActivity::class.java)
            startActivity(intent)
        }

        binding.ledMatrix.setOnClickListener {
            val intent = Intent(this, LedMatrixActivity::class.java)
            startActivity(intent)
        }

        binding.livingRoomLamp.setOnClickListener {
            val intent = Intent(this, LightsActivity::class.java)
            startActivity(intent)
        }

        binding.christmasTree.setOnClickListener {
            val intent = Intent(this, ChristmasTreeActivity::class.java)
            startActivity(intent)
        }

        binding.kitchen.setOnClickListener {
            val intent = Intent(this, KitchenLightActivity::class.java)
            startActivity(intent)
        }

        binding.allOff.setOnClickListener { allOff() }

    }

    private fun allOff() {
        println("All off called.")
        LedStrip.echoClient.sendBroadCast("off")
    }
}