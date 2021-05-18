package com.darvader.smarthome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.matrix.activity.LedMatrixActivity
import com.darvader.smarthome.ledstrip.LedStripActivity
import kotlinx.android.synthetic.main.activity_smart_home.*

class SmartHomeActivity : AppCompatActivity() {

    companion object {
        val echoServer = EchoServer()
        val echoClient = EchoClient()
    }
    init {
        echoServer.start()
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

    }
}