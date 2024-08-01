package com.darvader.smarthome.circulation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darvader.smarthome.SmartHomeActivity
import com.darvader.smarthome.databinding.ActivityCirculationPumpBinding
import java.util.Timer
import java.util.TimerTask

class CirculationPumpActivity : AppCompatActivity() {
    val circulationPump = CirculationPump(this)

    public lateinit var binding: ActivityCirculationPumpBinding
    private val timer = Timer()

    init {
        SmartHomeActivity.echoServer.register(circulationPump)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCirculationPumpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.on.setOnClickListener { circulationPump.on() }
        binding.off.setOnClickListener { circulationPump.off() }
        binding.circ5Min.setOnClickListener { circulationPump.circ5Min() }

        val task = object : TimerTask() {
            override fun run() {
                circulationPump.status()
            }
        }

        timer.scheduleAtFixedRate(task, 0, 5000)

    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }
}